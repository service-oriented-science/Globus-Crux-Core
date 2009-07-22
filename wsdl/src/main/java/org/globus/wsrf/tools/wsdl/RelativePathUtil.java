/*
 * Copyright 1999-2006 University of Chicago
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.globus.wsrf.tools.wsdl;

import java.io.File;

import java.util.LinkedList;


public class RelativePathUtil {
    /**
     * Returns the name of one file relative to another.  The name it returns
     * uses forward slashes "/" instead of the system specific file separator.
     */
    public static String getRelativeFileName(
        File target,
        File realativeTo
    ) {
        LinkedList targetList = getPathList(target);
        LinkedList relativeList = getPathList(realativeTo);

        while (
            !targetList.isEmpty() && !relativeList.isEmpty() &&
                targetList.getFirst().equals(relativeList.getFirst())
        ) {
            targetList.removeFirst();
            relativeList.removeFirst();
        }

        StringBuffer fileName = new StringBuffer();

        while (!relativeList.isEmpty()) {
            fileName.append("../");
            relativeList.removeFirst();
        }

        while (!targetList.isEmpty()) {
            fileName.append(targetList.removeFirst());
            fileName.append('/');
        }

        fileName.append(target.getName());

        return fileName.toString();
    }

    private static LinkedList getPathList(File file) {
        if (!file.isAbsolute()) {
            file = file.getAbsoluteFile();
        }

        LinkedList list = new LinkedList();

        if (!file.isDirectory()) {
            file = file.getParentFile();
        }

        while (file != null) {
            if (file.getName().length() > 0) { //getName() returns "" for the root dir 
                list.addFirst(file.getName());
            }

            file = file.getParentFile();
        }

        return list;
    }
}
