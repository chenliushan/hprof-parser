package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;

import java.util.ArrayList;
import java.util.List;

public class GroupClassAllocSite extends AllocSite {
    List<AllocSite> allocSiteList = new ArrayList<>();
    static String format = "%-15s  %-90s  %25s %25s %25s %25s";
    double convert = 0.000001;

    public GroupClassAllocSite(AllocSite allocSite) {
        super(allocSite.arrayIndicator, allocSite.classSerialNum, allocSite.stackTraceSerialNum, allocSite.numLiveBytes,
                allocSite.numLiveInstances, allocSite.numBytesAllocated, allocSite.numInstancesAllocated);
        allocSiteList.add(allocSite);
    }

    public void addAllocSite(AllocSite allocSite) {
        if (this.classSerialNum == allocSite.classSerialNum) {
            allocSiteList.add(allocSite);
            this.numLiveBytes += allocSite.numLiveBytes;
            this.numLiveInstances += allocSite.numLiveInstances;
            this.numBytesAllocated += allocSite.numBytesAllocated;
            this.numInstancesAllocated += allocSite.numInstancesAllocated;
        }
    }

    @Override
    public String toString() {
        return String.format(format, classSerialNum, LoadedClass.getClassName(classSerialNum),
                numBytesAllocated * convert, numInstancesAllocated, numLiveBytes * convert, numLiveInstances);
    }

    public void getMethods(String keyWord) {
        for (AllocSite allocSite : allocSiteList) {
            travelTrace(allocSite, keyWord, false);
        }
    }

    public static void travelTrace(AllocSite allocSite, String keyWord, boolean firstOnly) {
        HprofTrace trace = HprofTrace.traceMap.get(allocSite.stackTraceSerialNum);
        System.out.println(trace.info());
        for (HprofFrame hprofFrame : trace.getFrames()) {
            if (!keyWord.trim().equals("-") && hprofFrame.toString().toLowerCase().contains(keyWord.toLowerCase())) {
                System.out.println(hprofFrame.toString());
                if (firstOnly) break;
            }

        }
    }
}
