package hk.polyu.comp;

import edu.tufts.eaftan.hprofparser.handler.NullRecordHandler;
import edu.tufts.eaftan.hprofparser.parser.datastructures.ClassInfo;

import java.util.HashMap;

public class MyHandler extends NullRecordHandler {
   static HashMap<Long, String> idStringMap = new HashMap<Long, String>();
   static HashMap<Long, ClassInfo> idClassMap = new HashMap<Long, ClassInfo>();

}
