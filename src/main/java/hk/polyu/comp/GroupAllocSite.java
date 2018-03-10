package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;

import java.util.ArrayList;
import java.util.List;

public class GroupAllocSite extends AllocSite {
    final static String KEY_WORD = "polyu";
    final static String format = "%-15s  %-90s  %25s %25s %25s %25s";
    final static double convert = 0.000001;

    List<AllocSite> allocSiteList = new ArrayList<>();

    public GroupAllocSite(AllocSite allocSite) {
        super(allocSite.arrayIndicator, allocSite.classSerialNum, allocSite.stackTraceSerialNum, allocSite.numLiveBytes,
                allocSite.numLiveInstances, allocSite.numBytesAllocated, allocSite.numInstancesAllocated);
        allocSiteList.add(allocSite);
    }

    public void addAllocSite(AllocSite allocSite) {
        if (allocSite.numLiveBytes < 0 || allocSite.numBytesAllocated < 0)
            return;
        allocSiteList.add(allocSite);
        this.numLiveBytes += allocSite.numLiveBytes;
        this.numLiveInstances += allocSite.numLiveInstances;
        this.numBytesAllocated += allocSite.numBytesAllocated;
        this.numInstancesAllocated += allocSite.numInstancesAllocated;
    }

    public String getFormatString(String groupKey) {
        return String.format(format, classSerialNum, groupKey,
                numBytesAllocated * convert, numInstancesAllocated, numLiveBytes * convert, numLiveInstances);
    }

    public static void travelTrace(AllocSite allocSite, String keyWord, boolean firstOnly) {
        HprofTrace trace = HprofTrace.traceMap.get(allocSite.stackTraceSerialNum);
        System.out.println(trace.info());
        for (HprofFrame hprofFrame : trace.getFrames()) {
            if (keyWord.trim().equals("-") || hprofFrame.toString().toLowerCase().contains(keyWord.toLowerCase())) {
                System.out.println(hprofFrame.toString());
                if (firstOnly) break;
            }

        }
    }

    public static String getFirstFrameContainsKeyword(AllocSite allocSite, String keyWord) {
        HprofTrace trace = HprofTrace.traceMap.get(allocSite.stackTraceSerialNum);
        for (HprofFrame hprofFrame : trace.getFrames()) {
            if (hprofFrame.toString().toLowerCase().contains(keyWord.toLowerCase())) {
                return hprofFrame.toString();
            }

        }

        if (trace.getFrames().size() > 0)
            return trace.getFrames().get(0).toString();
        else
            return "Others";
    }

}
