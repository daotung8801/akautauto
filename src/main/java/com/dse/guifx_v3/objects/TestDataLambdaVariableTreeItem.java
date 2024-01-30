package com.dse.guifx_v3.objects;

import com.dse.logger.AkaLogger;
import com.dse.testdata.object.DataNode;
import com.dse.testdata.object.LambdaRootDataNode;
import com.dse.testdata.object.RootDataNode;
import com.dse.testdata.object.ValueDataNode;
import com.dse.util.NodeType;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

public class TestDataLambdaVariableTreeItem extends TestDataTreeItem{
    //todo: tao table cho lambda
    private final static AkaLogger logger = AkaLogger.get(TestDataLambdaVariableTreeItem.class);
    private ValueDataNode inputDataNode;
    private ValueDataNode expectedOutputDataNode;
    private List<TreeItem<DataNode>> inputChildren = new ArrayList<>();
    private List<TreeItem<DataNode>> expectedOutputChildren = new ArrayList<>();

    private ColumnType selectedColumn = ColumnType.INPUT;

    public TestDataLambdaVariableTreeItem(ValueDataNode dataNode) {
        super(dataNode);
        setColumnType(ColumnType.ALL);

        if (dataNode.getParent() instanceof LambdaRootDataNode
                && ((RootDataNode) dataNode.getParent()).getLevel().equals(NodeType.LAMBDA)) {
            LambdaRootDataNode parent = (LambdaRootDataNode) dataNode.getParent();
            this.inputDataNode = dataNode;
            this.expectedOutputDataNode = parent.getLambdaInputExpOutputMap().get(dataNode);
            if (expectedOutputDataNode == null) {
                logger.debug("Failed on get ExpectedOutputDataNode. Size of map: " + parent.getLambdaInputExpOutputMap().size());
            }
        }
    }

    public ColumnType getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(ColumnType selectedColumn) {
        if (selectedColumn != this.selectedColumn) {
            if (this.selectedColumn == ColumnType.INPUT) {
                // save children
                inputChildren.clear();
                inputChildren.addAll(getChildren());

                // switch value and children
                setValue(expectedOutputDataNode);
                getChildren().clear();
                getChildren().addAll(expectedOutputChildren);
            } else if (this.selectedColumn == ColumnType.EXPECTED) {
                // save children
                expectedOutputChildren.clear();
                expectedOutputChildren.addAll(getChildren());

                // switch value and children
                setValue(inputDataNode);
                getChildren().clear();
                getChildren().addAll(inputChildren);
            }

            this.selectedColumn = selectedColumn;
        }
    }

    public ValueDataNode getInputDataNode() {
        return inputDataNode;
    }

    public void setInputDataNode(ValueDataNode inputDataNode) {
        this.inputDataNode = inputDataNode;
    }

    public ValueDataNode getExpectedOutputDataNode() {
        return expectedOutputDataNode;
    }

    public void setExpectedOutputDataNode(ValueDataNode expectedOutputDataNode) {
        this.expectedOutputDataNode = expectedOutputDataNode;
    }
}
