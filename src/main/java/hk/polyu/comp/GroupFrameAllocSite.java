package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;

public class GroupFrameAllocSite extends GroupAllocSite {

    int count = 1;

    public GroupFrameAllocSite(AllocSite allocSite) {
        super(allocSite);
    }

    public void addAllocSite(AllocSite allocSite) {
        String firstFrameAlloc = getFirstFrameContainsKeyword(allocSite, KEY_WORD);
        String firstFramethis = getFirstFrameContainsKeyword(this, KEY_WORD);
        if (firstFramethis.equals(firstFrameAlloc)) {
            super.addAllocSite(allocSite);
            count += 1;
        }
    }

    @Override
    public String toString() {
        return getFormatString(count + " " + getFirstFrameContainsKeyword(this, "polyu"));
    }


}
