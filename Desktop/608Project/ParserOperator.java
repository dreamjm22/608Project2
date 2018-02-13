import operator.*;
import storageManager.Disk;
import storageManager.MainMemory;
import storageManager.SchemaManager;
import parser.Node;

import java.util.HashMap;

public class ParserOperator {
    Disk disk;
    MainMemory mainMemory;
    SchemaManager schemaManager;

    HashMap<String, OperatorInterface> parserOperatorImplLib = new HashMap<>();

    public ParserOperator(Disk disk, MainMemory mainMemory, SchemaManager schemaManager) {
        this.disk = disk;
        this.mainMemory = mainMemory;
        this.schemaManager = schemaManager;
        constructParserOperatorImplLib();
    }

    public void parserOperatorProcessor(Node parserNode) {
        double startTime = disk.getDiskTimer();
        long startIOs = disk.getDiskIOs();
        OperatorInterface operatorInterface = parserOperatorImplLib.get(parserNode.getElement());
        operatorInterface.operatorImpl(parserNode.getChildren(), disk, mainMemory, schemaManager);
        System.out.print("Calculated elapse time = " + String.valueOf(disk.getDiskTimer() - startTime) + " ms" + "\n");
        System.out.print("Calculated Disk I/Os = " + String.valueOf(disk.getDiskIOs() - startIOs) + "\n");
    }

    private void constructParserOperatorImplLib() {
        parserOperatorImplLib.put("CREATE", new CreateOperatorImpl());
        parserOperatorImplLib.put("DELETE", new DeleteOperatorImpl());
        parserOperatorImplLib.put("DROP", new DropOperatorImpl());
        parserOperatorImplLib.put("INSERT", new InsertOperatorImpl());
        parserOperatorImplLib.put("SELECT", new SelectOperatorImpl());
    }
}
