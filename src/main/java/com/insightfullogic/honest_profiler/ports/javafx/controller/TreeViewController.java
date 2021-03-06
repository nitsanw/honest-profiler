/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.ports.javafx.controller;

import static com.insightfullogic.honest_profiler.core.aggregation.result.ItemType.ENTRY;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_CNT_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_SELF_TIME_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_CNT_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.COLUMN_TOTAL_TIME_PCT;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_COLLAPSEALLALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_EXPANDALL;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_FILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_BUTTON_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_INPUT_QUICKFILTER;
import static com.insightfullogic.honest_profiler.ports.javafx.util.ResourceUtil.INFO_TABLE_TREE;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandFully;
import static com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil.expandPartial;

import com.insightfullogic.honest_profiler.core.aggregation.grouping.FrameGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.grouping.ThreadGrouping;
import com.insightfullogic.honest_profiler.core.aggregation.result.Aggregation;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Node;
import com.insightfullogic.honest_profiler.core.aggregation.result.straight.Tree;
import com.insightfullogic.honest_profiler.ports.javafx.controller.filter.FilterDialogController;
import com.insightfullogic.honest_profiler.ports.javafx.util.TreeUtil;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.GraphicalShareTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.cell.MethodNameTreeTableCell;
import com.insightfullogic.honest_profiler.ports.javafx.view.tree.NodeTreeItem;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

/**
 * Controller for Views which display the contents of a {@link Tree} {@link Aggregation}.
 */
public class TreeViewController extends AbstractProfileViewController<Tree, Node>
{
    @FXML
    private Button filterButton;
    @FXML
    private Button expandAllButton;
    @FXML
    private Button collapseAllButton;
    @FXML
    private TextField quickFilterText;
    @FXML
    private Button quickFilterButton;

    @FXML
    private Label threadGroupingLabel;
    @FXML
    private ChoiceBox<ThreadGrouping> threadGrouping;
    @FXML
    private Label frameGroupingLabel;
    @FXML
    private ChoiceBox<FrameGrouping> frameGrouping;

    @FXML
    private TreeTableView<Node> treeTable;
    @FXML
    private TreeTableColumn<Node, String> methodColumn;
    @FXML
    private TreeTableColumn<Node, Number> percentColumn;
    @FXML
    private TreeTableColumn<Node, Number> totalCntPct;
    @FXML
    private TreeTableColumn<Node, Number> selfCntPct;
    @FXML
    private TreeTableColumn<Node, Number> totalCnt;
    @FXML
    private TreeTableColumn<Node, Number> selfCnt;
    @FXML
    private TreeTableColumn<Node, Number> totalTimePct;
    @FXML
    private TreeTableColumn<Node, Number> selfTimePct;
    @FXML
    private TreeTableColumn<Node, Number> totalTime;
    @FXML
    private TreeTableColumn<Node, Number> selfTime;

    @FXML
    private FilterDialogController<Node> filterController;

    // FXML Implementation

    @Override
    @FXML
    protected void initialize()
    {
        super.initialize(ENTRY);
        super.initialize(filterController, filterButton, quickFilterButton, quickFilterText);
        super.initialize(threadGroupingLabel, threadGrouping, frameGroupingLabel, frameGrouping);

        initializeTable();
    }

    // Instance Accessors

    /**
     * Returns the {@link ReadOnlyObjectProperty} tracking which item is currently selected.
     * <p>
     *
     * @return the {@link ReadOnlyObjectProperty} tracking which item is currently selected
     */
    public ReadOnlyObjectProperty<TreeItem<Node>> selectedProperty()
    {
        return treeTable.getSelectionModel().selectedItemProperty();
    }

    // AbstractController Implementation

    @Override
    protected void initializeInfoText()
    {
        info(filterButton, INFO_BUTTON_FILTER);
        info(expandAllButton, INFO_BUTTON_EXPANDALL);
        info(collapseAllButton, INFO_BUTTON_COLLAPSEALLALL);
        info(quickFilterText, INFO_INPUT_QUICKFILTER);
        info(quickFilterButton, INFO_BUTTON_QUICKFILTER);
        info(treeTable, INFO_TABLE_TREE);
    }

    @Override
    protected void initializeHandlers()
    {
        expandAllButton.setOnAction(event -> expandFully(treeTable.getRoot()));

        collapseAllButton.setOnAction(
            event -> treeTable.getRoot().getChildren().stream().forEach(TreeUtil::collapseFully));
    }

    // AbstractViewController Implementation

    @Override
    protected void refresh()
    {
        Tree target = getTarget();
        if (target == null)
        {
            treeTable.setRoot(null);
        }
        else
        {
            treeTable.setRoot(new NodeTreeItem(getTarget().filter(getFilterSpecification())));
            expandPartial(treeTable.getRoot(), 2);
        }
        treeTable.sort();
    }

    /**
     * Initializes the {@link TreeTableView} which displays the {@link Tree} {@link Aggregation}.
     */
    @Override
    protected void initializeTable()
    {
        methodColumn.setCellFactory(column -> new MethodNameTreeTableCell<>(appCtx()));
        methodColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("key"));

        percentColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("totalCntPct"));
        percentColumn.setCellFactory(param -> new GraphicalShareTreeTableCell());

        cfgPctCol(totalCntPct, "totalCntPct", prfCtx(), COLUMN_TOTAL_CNT_PCT);
        cfgPctCol(selfCntPct, "selfCntPct", prfCtx(), COLUMN_SELF_CNT_PCT);
        cfgNrCol(totalCnt, "totalCnt", prfCtx(), COLUMN_TOTAL_CNT);
        cfgNrCol(selfCnt, "selfCnt", prfCtx(), COLUMN_SELF_CNT);
        cfgPctCol(totalTimePct, "totalTimePct", prfCtx(), COLUMN_TOTAL_TIME_PCT);
        cfgPctCol(selfTimePct, "selfTimePct", prfCtx(), COLUMN_SELF_TIME_PCT);
        cfgTimeCol(totalTime, "totalTime", prfCtx(), COLUMN_TOTAL_TIME);
        cfgTimeCol(selfTime, "selfTime", prfCtx(), COLUMN_SELF_TIME);
    }
}
