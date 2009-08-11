/*
 * JavaScript cogkit
 */

function CogKit2(url){

/* the service endpoint url */
var agentURL = url;
var user;
var mySessionId = 0;

/*
 * send the authentication info through SOAP
 */
this.auth = function(host, port, username, password, authResponse){
user = username;
var pl = new SOAPClientParameters();
//set parameters to retrieve credential frm myproxy server
pl.add("host", host);
pl.add("port", port);
pl.add("user", user);
pl.add("password", password);
pl.add("lifetime", 0);//default 2 hr

try{
	SOAPClient.invoke(agentURL, "retrieveCertificate", pl, true, authResponseInner);
	}
catch(e){
	alert(e);
	}

function authResponseInner(r, soapResponse){
	var ret;
	try{
		ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
	} catch (e) {
		authResponse(false);
		return;
	}
	var sid = parseInt(ret);
	if(sid > 0){
		mySessionId = sid;
		authResponse(true);
	} else {
		authResponse(false);
	}
}
}

/*
 * construct Karajan workflow from job specifation
 */
this.constructRemoteJob = function(cmd, arg, rHost, stdout, provider){
    /* construct karajan workflow */
    var strProj = "<project>\n"+
      "<include file=\"cogkit.xml\"/>\n"+ 
      "<execute executable=\"" + cmd + "\" "+ 
                    "arguments=\"" + arg +"\" " +
                    "stdout=\"" + stdout + "\"\n"+
                    "\thost=\"" + rHost + "\" provider=\"" + provider + "\" redirect=\"false\"/>\n"+
      "<transfer srchost=\"" + rHost + "\" srcfile=\"" + stdout + "\"\n"+
                     "\tdesthost=\"localhost\" provider=\"gridftp\"/>\n"+
    "</project>";
    return strProj;
}

/* 
 * submit a karajan workflow
 */
this.submitWf = function(strProj, submitResponse){
	var pl = new SOAPClientParameters();
	pl.add("user", user);
	pl.add("proj", strProj);
	pl.add("token", mySessionId);
	try{
		SOAPClient.invoke(agentURL, "exec", pl, true, submitResponseInner);
	}catch(e){
		alert(e);
	}
	
function submitResponseInner(r, soapResponse) {
	var ret;
	try{
		ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
	} catch (e){
		submitResponse(0);
		return;
	}
	submitResponse(ret);
}
}

/*
 * send status query request
 */
this.statusQuery = function(wfid, statusQueryResponse){
	var pl = new SOAPClientParameters();	
	pl.add("user", user);
	pl.add("wfid", wfid);
	pl.add("token", mySessionId);
    try{
		SOAPClient.invoke(agentURL, "statusQuery", pl, true, statusQueryResponseInner);
	} catch(e) {
		alert(e);
	}
	
function statusQueryResponseInner(r, soapResponse){
	var ret;
	try{
	    ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
	} catch (e) {
	    statusQueryResponse(null);
	    return;
	}
	var delim = ret.indexOf("_");
	var wfid = ret.substring(0,delim);
	var status = parseInt(ret.substring(delim+1)) + 1;
	var jsonRet = "{\"wfid\":" + wfid + ",\"status\":" + status + "}";
	statusQueryResponse(jsonRet);
}

}

/*
 * list the submitted workflows of the current user
 */
this.listMyWf = function(listMyWfResponse){
	var pl = new SOAPClientParameters();
	pl.add("user", user);
	pl.add("token", mySessionId);
	try{
		SOAPClient.invoke(agentURL, "listExecIds", pl, true, listMyWfResponseInner);
	}catch(e){
		alert(e);
	}
	
function listMyWfResponseInner(r, soapResponse){	
	var ret;
	try{
		ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
	} catch (e) {
		listMyWfResponse(null);
		return;
	}
	var mySplitResults = ret.split("_");
	var numIds = mySplitResults.length - 1;
	var jsonRet = "{\"wfids\": [";
	for(i = 0; i < numIds - 1; i++){
		jsonRet += mySplitResults[i];
		jsonRet += ",";
	}
	jsonRet += mySplitResults[numIds - 1] + "]}";
	listMyWfResponse(jsonRet);
}

}

/*
 * send out request to retrieve the names of all the output files of a specified workflow
 */
this.listOutput = function(wfid, listOutputResponse){
	var pl = new SOAPClientParameters();
	pl.add("user", user);
	pl.add("wfid", wfid);
	pl.add("token", mySessionId);
	try{
		SOAPClient.invoke(agentURL, "listOutput", pl, true, listOutputResponseInner);
	}catch(e){
		alert(e);
	}
	
function listOutputResponseInner(r, soapResponse){
	var ret;
	try{
		ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
	} catch (e) {
		listOutputResponse(null);
		return;
	}
	var mySplitResult = ret.split("_");
	var numResults = mySplitResult.length;
	var wfid = parseInt(mySplitResult[0]);
	var jsonRet = "{\"wfid\":" + wfid + ", \"resultFiles\":[";
	for(i = 1; i < numResults - 1; i++){
		jsonRet += "\"" + mySplitResult[i] + "\",";
	}
	jsonRet += "\"" + mySplitResult[numResults - 1] + "\"]}";
	//alert(resultsFilename);
	listOutputResponse(jsonRet);
}

}

/*
 * fetch the content of an output file of a specified workflow
 */
this.displayResult = function(wfid, outputfile, fetchOutputResponse){
	var pl = new SOAPClientParameters();
	pl.add("user", user);
	pl.add("wfid", wfid);
	pl.add("outputfile", outputfile);
	pl.add("token", mySessionId);
    try{
		SOAPClient.invoke(url, "fetchOutput", pl, true, fetchOutputResponseInner);
	} catch(e) {
		alert(e);
	}
}

function fetchOutputResponseInner(r, soapResponse){
	var ret;
	try{
		ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
	} catch (e) {
		fetchOutputResponse(null);
		return;
	}
	var delimPos = ret.indexOf("_");
	var wfid = ret.substring(0, delimPos);
	var file = ret.substring(delimPos + 1);
	delimPos = file.indexOf("_");
	var filename = file.substring(0, delimPos);
	var content = escape(file.substring(delimPos + 1));
	var jsonRet = "{\"wfid\":\"" + wfid + "\",\"filename\":\"" + filename + "\",\"content\":\"" + content + "\"}";
	fetchOutputResponse(jsonRet);
}

}
