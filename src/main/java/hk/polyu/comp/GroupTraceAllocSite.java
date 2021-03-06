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
        return getFormatString(getFirstFrameContainsKeyword(this, "polyu"));
    }


}
