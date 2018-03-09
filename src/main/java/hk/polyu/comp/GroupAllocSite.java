package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;

import java.util.ArrayList;
import java.util.List;

public class GroupAllocSite extends AllocSite {
    List<AllocSite> allocSiteList = new ArrayList<>();
    static String format = "%-15s  %-90s  %25s %25s %25s %25s";
    double convert = 0.000001;

    public GroupAllocSite(AllocSite allocSite) {
        super(allocSite.arrayIndicator, allocSite.classSerialNum, allocSite.stackTraceSerialNum, allocSite.numLiveBytes,
                allocSite.numLiveInstances, allocSite.numBytesAllocated, allocSite.numInstancesAllocated);
        allocSiteList.add(allocSite);
    }

    public void addAllocSite(AllocSite allocSite) {
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


}
