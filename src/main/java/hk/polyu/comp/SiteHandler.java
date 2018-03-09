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

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;
import edu.tufts.eaftan.hprofparser.parser.datastructures.CPUSample;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static hk.polyu.comp.HprofTrace.construct;

/**
 * Prints details for each record encountered.
 */
public class SiteHandler extends MyHandler {
    List<AllocSite> allocSites = new ArrayList<>();


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
    public void allocSites(short bitMaskFlags, float cutoffRatio,
                           int totalLiveBytes, int totalLiveInstances, long totalBytesAllocated,
                           long totalInstancesAllocated, AllocSite[] sites) {
        System.out.println("Alloc Sites:");
        System.out.println("    bit mask flags: " + bitMaskFlags);
        System.out.println("    incremental vs. complete: " + testBitMask(bitMaskFlags, 0x1));
        System.out.println("    sorted by allocation vs. line: " + testBitMask(bitMaskFlags, 0x2));
        System.out.println("    whether to force GC: " + testBitMask(bitMaskFlags, 0x4));
        System.out.println("    cutoff ratio: " + cutoffRatio);
        System.out.println("    total live bytes: " + totalLiveBytes);
        System.out.println("    total live instances: " + totalLiveInstances);
        System.out.println("    total bytes allocated: " + totalBytesAllocated);
        System.out.println("    total instances allocated: " + totalInstancesAllocated);
        Collections.addAll(allocSites, sites);
    }

    @Override
    public void heapSummary(int totalLiveBytes, int totalLiveInstances,
                            long totalBytesAllocated, long totalInstancesAllocated) {
        System.out.println("Heap Summary:");
        System.out.println("    total live bytes: " + totalLiveBytes);
        System.out.println("    total live instances: " + totalLiveInstances);
        System.out.println("    total bytes allocated: " + totalBytesAllocated);
        System.out.println("    total instances allocated: " + totalInstancesAllocated);
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
    public void finished() {
        super.finished();

        groupByTrace();
    }


    private void groupByTrace() {
        Map<Integer, GroupTraceAllocSite> groupByTrace = new HashMap<>();
        for (AllocSite allocSite : allocSites) {
            if (groupByTrace.containsKey(allocSite.stackTraceSerialNum)) {
                groupByTrace.get(allocSite.stackTraceSerialNum).addAllocSite(allocSite);
            } else {
                groupByTrace.put(allocSite.stackTraceSerialNum, new GroupTraceAllocSite(allocSite));
            }
        }
        List<GroupTraceAllocSite> sortedGroup = groupByTrace.values().stream()
                .sorted((c1, c2) -> (c2.numBytesAllocated - c1.numBytesAllocated))
                .collect(Collectors.toList());
        System.out.println(String.format(GroupTraceAllocSite.format, "stackTraceSerialNum", "firstFrame",
                "numBytesAllocated", "numInstancesAllocated", "numLiveBytes", "numLiveInstances"));
        sortedGroup.forEach(x -> System.out.println(x.toString()));
        System.out.println(sortedGroup.size());

    }

    private void groupBysite() {
        List<GroupClassAllocSite> sortedGroup = groupSite();
        Scanner scanner = new Scanner(System.in);
        Map<Integer, GroupClassAllocSite> groupedAllocSiteMap = sortedGroup.stream().collect(Collectors.toMap(x -> x.classSerialNum, x -> x));
        while (true) {
            printSite(sortedGroup);
            System.out.print("\nEnter classSerialNum to print: ");
            int classSerialNum = scanner.nextInt();
            System.out.print("\nEnter keyword: ");
            String keyWord = scanner.next();
            GroupClassAllocSite groupClassAllocSite = groupedAllocSiteMap.get(classSerialNum);
            if (groupClassAllocSite != null) groupClassAllocSite.getMethods(keyWord);
        }
    }

    private void printSite(List<GroupClassAllocSite> sortedGroup) {
        System.out.println(String.format(GroupClassAllocSite.format, "classSerialNum", "ClassNames",
                "numBytesAllocated", "numInstancesAllocated", "numLiveBytes", "numLiveInstances"));
        sortedGroup.forEach(x -> System.out.println(x.toString()));
        System.out.println(sortedGroup.size());
    }

    private List<GroupClassAllocSite> groupSite() {

        Map<Integer, GroupClassAllocSite> groupedAllocSites = new HashMap<>();
        for (AllocSite allocSite : allocSites) {
            if (groupedAllocSites.containsKey(allocSite.classSerialNum)) {
                groupedAllocSites.get(allocSite.classSerialNum).addAllocSite(allocSite);
            } else {
                groupedAllocSites.put(allocSite.classSerialNum, new GroupClassAllocSite(allocSite));
            }
        }
        return groupedAllocSites.values().stream()
                .sorted((c1, c2) -> (c2.numBytesAllocated - c1.numBytesAllocated))
                .collect(Collectors.toList());


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
