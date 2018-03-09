/*
 * Copyright 2014 Edward Aftandilian. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static edu.tufts.eaftan.hprofparser.parser.datastructures.Type.hprofTypeToEnum;
import static hk.polyu.comp.HprofTrace.construct;

/**
 * Prints details for each record encountered.
 */
public class DumpHandler extends MyHandler {


    HashMap<Long, Integer> instanceCount = new HashMap<>();
    HashMap<Long, Integer> objectArrayCount = new HashMap<>();
    HashMap<Byte, Integer> primitiveArrayCount = new HashMap<>();


  /* handler for file header */

    @Override
    public void header(String format, int idSize, long time) {
        System.out.println(format);
        System.out.println(idSize);
        System.out.println(millisecondsDateToString(time));
    }
  

  /* Handlers for top-level records */

    @Override
    public void stringInUTF8(long id, String data) {
        // store string for later lookup
        idStringMap.put(id, data);
    }

    @Override
    public void loadClass(int classSerialNum, long classObjId,
                          int stackTraceSerialNum, long classNameStringId) {
        LoadedClass.construct(classSerialNum, classObjId, stackTraceSerialNum, classNameStringId);
    }

    @Override
    public void unloadClass(int classSerialNum) {
    }

    @Override
    public void stackTrace(int stackTraceSerialNum, int threadSerialNum,
                           int numFrames, long[] stackFrameIds) {
        construct(stackTraceSerialNum, threadSerialNum, numFrames, stackFrameIds);
    }

    @Override
    public void stackFrame(long stackFrameId, long methodNameStringId,
                           long methodSigStringId, long sourceFileNameStringId,
                           int classSerialNum, int location) {
        HprofFrame.construct(stackFrameId, methodNameStringId, methodSigStringId, sourceFileNameStringId, classSerialNum, location);
    }


    @Override
    public void startThread(int threadSerialNum, long threadObjectId,
                            int stackTraceSerialNum, long threadNameStringId, long threadGroupNameId,
                            long threadParentGroupNameId) {
    }

    @Override
    public void endThread(int threadSerialNum) {
    }

    @Override
    public void heapDump() {
        System.out.println("Heap Dump:");
    }

    @Override
    public void heapDumpEnd() {
        System.out.println("Heap Dump End:");
    }

    @Override
    public void heapDumpSegment() {
        System.out.println("Heap Dump Segment:");
    }

    @Override
    public void cpuSamples(int totalNumOfSamples, CPUSample[] samples) {
        System.out.println("CPU Samples:");
        System.out.println("    total num of samples: " + totalNumOfSamples);
        for (int i = 0; i < samples.length; i++) {
            System.out.println("        cpu sample " + (i + 1) + ":");
            System.out.println("            number of samples: " + samples[i].numSamples);
            System.out.println("            stack trace serial num: " + samples[i].stackTraceSerialNum);
        }
    }

    @Override
    public void controlSettings(int bitMaskFlags, short stackTraceDepth) {
        System.out.println("Control Settings:");
        System.out.println("    bit mask flags: " + bitMaskFlags);
        System.out.println("    alloc traces on/off: " + testBitMask(bitMaskFlags, 0x1));
        System.out.println("    cpu sampling on/off: " + testBitMask(bitMaskFlags, 0x2));
        System.out.println("    stack trace depth: " + stackTraceDepth);
    }


    /* Handlers for heap dump records */
    @Override
    public void rootMonitorUsed(long objId) {
        System.out.println("Root Monitor Used:" + idStringMap.get(objId));
    }


    @Override
    public void classDump(long classObjId, int stackTraceSerialNum,
                          long superClassObjId, long classLoaderObjId, long signersObjId,
                          long protectionDomainObjId, long reserved1, long reserved2,
                          int instanceSize, Constant[] constants, Static[] statics,
                          InstanceField[] instanceFields) {
        // store class info in a hashmap for later access
        idClassMap.put(classObjId, new ClassInfo(classObjId, superClassObjId, instanceSize,
                instanceFields));
    }

    @Override
    public void instanceDump(long objId, int stackTraceSerialNum,
                             long classObjId, Value<?>[] instanceFieldValues) {
        if (instanceCount.containsKey(classObjId))
            instanceCount.put(classObjId, instanceCount.get(classObjId) + 1);
        else
            instanceCount.put(classObjId, 1);
    }

    @Override
    public void objArrayDump(long objId, int stackTraceSerialNum,
                             long elemClassObjId, long[] elems) {
        if (objectArrayCount.containsKey(elemClassObjId))
            objectArrayCount.put(elemClassObjId, objectArrayCount.get(elemClassObjId) + elems.length);
        else
            objectArrayCount.put(elemClassObjId, elems.length);
    }

    @Override
    public void primArrayDump(long objId, int stackTraceSerialNum,
                              byte elemType, Value<?>[] elems) {
        if (primitiveArrayCount.containsKey(elemType))
            primitiveArrayCount.put(elemType, primitiveArrayCount.get(elemType) + elems.length);
        else
            primitiveArrayCount.put(elemType, elems.length);

    }

    @Override
    public void finished() {
        super.finished();
        printDump();
    }

    private void printDump() {
        System.out.println("\n\ninstanceCount:");
        instanceCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(100)
                .forEach(x ->
                        System.out.println(LoadedClass.getClassName(x.getKey()) + "::" + x.getValue())
                );
        System.out.println("\n\nobjectArrayCount:");
        objectArrayCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(100)
                .forEach(x ->
                        System.out.println(LoadedClass.getClassName(x.getKey()) + "::" + x.getValue())
                );
        System.out.println("\n\nprimitiveArrayCount:");
        primitiveArrayCount.entrySet().stream()
                .sorted(Map.Entry.<Byte, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(x ->
                        System.out.println(hprofTypeToEnum(x.getKey()) + "::" + x.getValue())
                );
//        System.out.println("idClassMap:");
//        idClassMap.values().stream().filter(x -> (x.instanceSize > 0 && LoadedClass.getClassName(x.classObjId).toLowerCase().contains("map")))
//                .sorted((c1, c2) -> (c2.instanceSize - c1.instanceSize))
//                .collect(Collectors.toList())
//                .forEach(c -> System.out.println(LoadedClass.getClassName(c.classObjId) + "::" + c.instanceSize));
    }


    /* Utility methods */

    private static boolean testBitMask(int bitMaskFlags, int mask) {
        if ((bitMaskFlags & mask) != 0)
            return true;
        else
            return false;
    }


    private static String millisecondsDateToString(long milliseconds) {

        SimpleDateFormat formatter =
                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        return formatter.format(calendar.getTime());
    }


}
