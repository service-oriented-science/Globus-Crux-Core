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
 * list user's remote home dir in Mediator server
 */
this.listDir = function(loc, dir, listDirResponse){
var pl = new SOAPClientParameters();
//set parameters to retrieve credential frm myproxy server
pl.add("user", user);
pl.add("token", mySessionId);
pl.add("dir", dir);

try{
	SOAPClient.invoke(agentURL, "listDir", pl, true, listDirResponseInner);
	}
catch(e){
	alert(e);
	}

function listDirResponseInner(r, soapResponse){
	var ret;
	try{
	    ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
	} catch (e) {
	    listDirResponse(loc, null);
	    return;
	}
	var mySplitResults = ret.split("\n");
	var numIds = mySplitResults.length - 1;
	mySplitResults[0] = "";
	var jsonRet = "{\"filelists\": [";
	for(i = 1; i < numIds - 1; i++){
		jsonRet = jsonRet + "\"" + mySplitResults[i] + "\"";
		jsonRet = jsonRet + ",";
	}
	jsonRet = jsonRet + "\"" + mySplitResults[numIds - 1] + "\"]}";
	listDirResponse(loc, jsonRet);
}

}

/*
 * construct Karajan workflow from job specifation
 */
this.constructRemoteJob = function(cmd, arg, rHost, stdout, filetotransfer, provider){
    /* construct karajan workflow */
    var strProj = "<project>\n"+
      "<include file=\"cogkit.xml\"/>\n"+ 
      "<execute executable=\"" + cmd + "\" "+ 
                    "arguments=\"" + arg +"\" " +
                    "stdout=\"" + stdout + "\"\n"+
                    "\thost=\"" + rHost + "\" provider=\"" + provider + "\" redirect=\"false\"/>\n"+
      "<transfer srchost=\"" + rHost + "\" srcfile=\"" + filetotransfer + "\"\n"+
                     "\tdesthost=\"localhost\" provider=\"gridftp\"/>\n"+
    "</project>";
    return strProj;
}

/* 
 * submit a karajan workflow
 */
this.submitWf = function(strProj, submitResponse, loc){
	var pl = new SOAPClientParameters();
	pl.add("user", user);
	pl.add("token", mySessionId);
	pl.add("proj", strProj);
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
	if(ret == 0){
		submitResponse(0);
	} else {
		submitResponse(ret, loc);
	}
}
}

	/** 
	 * Carry on a file transfer. Internally a Grid job is constructed and submitted.
	 * @param {String} source Source URI for the file to be transfered
	 * @param {String} destination Destination URI of the file's new loc
	 * @param {String} transferCallback Optional parameter exists only for asynchronous invoke mode. If ommitted a synchronous call will be fired.
	 * @param {String} updateloc Indicate which side should be refreshed.
	 * @return {{"wfid":String}} A JSON object containing the transfer job's jobID. Only return for synchronous invoke mode.
	 */
	this.transfer = function(source, destination, transferCallback, updateloc){
		//console.info("in jscog: " + updateloc);
		var pl = new SOAPClientParameters();	
		pl.add("user", user);
		pl.add("token", mySessionId);
		pl.add("from", source);
		pl.add("to", destination);
		try{
			SOAPClient.invoke(agentURL, "transfer", pl, true, transferResponseInner);
		} catch(e) {
			alert(e);
		}
	
		function transferResponseInner(r, soapResponse){
			//console.info("in jscog inner: " + updateloc);
			var ret;
			try{
				ret = soapResponse.getElementsByTagNameNS("*","return")[0].childNodes[0].nodeValue;
				//console.info(ret);
			} catch (e) {				
				//console.info("exception in file transfer");
				transferCallback(null, updateloc);
				return;
			}
			transferCallback(ret, updateloc);
		}
	}


/*
 * send status query request
 */
this.statusQuery = function(wfid, statusQueryResponse, loc){
	var pl = new SOAPClientParameters();	
	pl.add("user", user);
	pl.add("token", mySessionId);
	pl.add("wfid", wfid);
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
	if(ret == 0){
		statusQueryResponse(null);
	    return;
	}
	var delim = ret.indexOf("_");
	var wfid = ret.substring(0,delim);
	var status = parseInt(ret.substring(delim+1)) + 1;
	var jsonRet = "{\"wfid\":" + wfid + ",\"status\":" + status + "}";
	statusQueryResponse(jsonRet, loc);
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
	pl.add("token", mySessionId);
	pl.add("wfid", wfid);
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
this.displayResult = function(wfid, outputfile, fetchOutputResponse, loc){
	//console.info("in cog display result")
	//console.info(arguments);
	//console.info(arguments.length);
	
	var pl = new SOAPClientParameters();
	pl.add("user", user);
	pl.add("token", mySessionId);
	pl.add("wfid", wfid);
	pl.add("outputfile", outputfile);
    try{
		SOAPClient.invoke(url, "fetchOutput", pl, true, fetchOutputResponseInner);
	} catch(e) {
		alert(e);
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
	if(location != undefined){
		fetchOutputResponse(jsonRet, loc);
	} else {
		fetchOutputResponse(jsonRet);
	}
}//inner

}//displayResult

}//cog
