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
