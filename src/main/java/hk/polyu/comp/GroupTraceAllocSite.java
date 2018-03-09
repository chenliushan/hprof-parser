package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;

public class GroupTraceAllocSite extends GroupAllocSite {
    public GroupTraceAllocSite(AllocSite allocSite) {
        super(allocSite);
    }

    public void addAllocSite(AllocSite allocSite) {
        if (this.stackTraceSerialNum == allocSite.stackTraceSerialNum) {
            super.addAllocSite(allocSite);
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
        return getFormatString(firstFrame);
    }


}
