package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;

import java.util.ArrayList;
import java.util.List;

public class GroupTraceAllocSite extends AllocSite {
    List<AllocSite> allocSiteList = new ArrayList<>();
    static String format = "%-15s  %-90s  %25s %25s %25s %25s";
    double convert = 0.000001;

    public GroupTraceAllocSite(AllocSite allocSite) {
        super(allocSite.arrayIndicator, allocSite.classSerialNum, allocSite.stackTraceSerialNum, allocSite.numLiveBytes,
                allocSite.numLiveInstances, allocSite.numBytesAllocated, allocSite.numInstancesAllocated);
        allocSiteList.add(allocSite);
    }

    public void addAllocSite(AllocSite allocSite) {
        if (this.stackTraceSerialNum == allocSite.stackTraceSerialNum) {
            allocSiteList.add(allocSite);
            this.numLiveBytes += allocSite.numLiveBytes;
            this.numLiveInstances += allocSite.numLiveInstances;
            this.numBytesAllocated += allocSite.numBytesAllocated;
            this.numInstancesAllocated += allocSite.numInstancesAllocated;
        }
    }

    @Override
    public String toString() {
        HprofTrace trace = HprofTrace.traceMap.get(stackTraceSerialNum);
        String firstFrame = "";
        for (HprofFrame hprofFrame : trace.getFrames()) {
            if (hprofFrame.toString().toLowerCase().contains("polyu")) {
                firstFrame = hprofFrame.toString();
                break;
            }

        }
        return String.format(format, stackTraceSerialNum, firstFrame,
                numBytesAllocated * convert, numInstancesAllocated , numLiveBytes * convert, numLiveInstances );
    }

    public void printDetails() {
        HprofTrace trace = HprofTrace.traceMap.get(stackTraceSerialNum);
        System.out.println(trace.toString());

        for (AllocSite allocSite : allocSiteList) {

            System.out.println(String.format(format, allocSite.classSerialNum, LoadedClass.getClassName(allocSite.classSerialNum),
                    allocSite.numBytesAllocated * convert, allocSite.numInstancesAllocated * convert,
                    allocSite.numLiveBytes * convert, allocSite.numLiveInstances * convert));
        }
    }


}
