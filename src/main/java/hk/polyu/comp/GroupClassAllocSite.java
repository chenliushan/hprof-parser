package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.parser.datastructures.AllocSite;

public class GroupClassAllocSite extends GroupAllocSite {

    public GroupClassAllocSite(AllocSite allocSite) {
        super(allocSite);
    }

    public void addAllocSite(AllocSite allocSite) {
        if (this.classSerialNum == allocSite.classSerialNum)
            super.addAllocSite(allocSite);
    }

    @Override
    public String toString() {
        return getFormatString(LoadedClass.getClassName(classSerialNum));
    }

    public void getMethods(String keyWord) {
        for (AllocSite allocSite : allocSiteList) {
            travelTrace(allocSite, keyWord, false);
        }
    }


}
