package com.benny.openlauncher.activity.homeparts;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.interfaces.DropTargetListener;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.model.Item;
import com.benny.openlauncher.viewutil.PopupIconLabelItem;
import com.benny.openlauncher.util.Definitions;
import com.benny.openlauncher.util.DragAction;
import com.benny.openlauncher.util.DragAction.Action;
import com.benny.openlauncher.util.Tool;
import com.benny.openlauncher.widget.CellContainer;
import com.benny.openlauncher.widget.Desktop;
import com.benny.openlauncher.widget.ItemOptionView;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.ArrayList;

public class HpDragOption {
    private final int uninstallItemIdentifier = 83;
    private final int infoItemIdentifier = 84;
    private final int editItemIdentifier = 85;
    private final int removeItemIdentifier = 86;

    private PopupIconLabelItem uninstallItem = new PopupIconLabelItem(R.string.uninstall, R.drawable.ic_delete_dark_24dp).withIdentifier(uninstallItemIdentifier);
    private PopupIconLabelItem infoItem = new PopupIconLabelItem(R.string.info, R.drawable.ic_info_outline_dark_24dp).withIdentifier(infoItemIdentifier);
    private PopupIconLabelItem editItem = new PopupIconLabelItem(R.string.edit, R.drawable.ic_edit_black_24dp).withIdentifier(editItemIdentifier);
    private PopupIconLabelItem removeItem = new PopupIconLabelItem(R.string.remove, R.drawable.ic_close_dark_24dp).withIdentifier(removeItemIdentifier);

    public void initDragNDrop(@NonNull final HomeActivity _homeActivity, @NonNull final View leftDragHandle, @NonNull final View rightDragHandle, @NonNull final ItemOptionView dragNDropView) {
        final Handler dragHandler = new Handler();

        dragNDropView.registerDropTarget(new DropTargetListener() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int i = _homeActivity.getDesktop().getCurrentItem();
                    if (i > 0) {
                        _homeActivity.getDesktop().setCurrentItem(i - 1);
                    } else if (i == 0) {
                        _homeActivity.getDesktop().addPageLeft(true);
                    }
                    dragHandler.postDelayed(this, 1000);
                }
            };

            @Override
            public View getView() {
                return leftDragHandle;
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                leftDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnter(Action action, PointF location) {
                dragHandler.post(runnable);
                leftDragHandle.animate().alpha(0.9f);
            }

            @Override
            public void onMove(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                // do nothing
            }

            @Override
            public void onExit(Action action, PointF location) {
                dragHandler.removeCallbacksAndMessages(null);
                leftDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnd() {
                dragHandler.removeCallbacksAndMessages(null);
                leftDragHandle.animate().alpha(0f);
            }
        });

        dragNDropView.registerDropTarget(new DropTargetListener() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int i = _homeActivity.getDesktop().getCurrentItem();
                    if (i < _homeActivity.getDesktop().getPages().size() - 1) {
                        _homeActivity.getDesktop().setCurrentItem(i + 1);
                    } else if (i == _homeActivity.getDesktop().getPages().size() - 1) {
                        _homeActivity.getDesktop().addPageRight(true);
                    }
                    dragHandler.postDelayed(this, 1000);
                }
            };

            @Override
            public View getView() {
                return rightDragHandle;
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                rightDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnter(Action action, PointF location) {
                dragHandler.post(runnable);
                rightDragHandle.animate().alpha(0.9f);
            }

            @Override
            public void onMove(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                // do nothing
            }

            @Override
            public void onExit(Action action, PointF location) {
                dragHandler.removeCallbacksAndMessages(null);
                rightDragHandle.animate().alpha(0.5f);
            }

            @Override
            public void onEnd() {
                dragHandler.removeCallbacksAndMessages(null);
                rightDragHandle.animate().alpha(0f);
            }
        });

        // desktop drag event
        dragNDropView.registerDropTarget(new DropTargetListener() {
            @Override
            public View getView() {
                return _homeActivity.getDesktop();
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                if (!DragAction.Action.SEARCH.equals(action))
                    showItemPopup(dragNDropView, _homeActivity);
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                _homeActivity.closeAppDrawer();
                _homeActivity.getSearchBar().collapse();
                if (Setup.appSettings().isDesktopShowGrid()) {
                    _homeActivity.getDock().setHideGrid(false);
                    for (CellContainer cellContainer : _homeActivity.getDesktop().getPages()) {
                        cellContainer.setHideGrid(false);
                    }
                }
            }

            @Override
            public void onEnter(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                // this statement makes sure that adding an app multiple times from the app drawer works
                // the app will get a new id every time
                if (DragAction.Action.DRAWER.equals(action)) {
                    if (_homeActivity.getAppDrawerController()._isOpen) {
                        return;
                    }
                    item.reset();
                }

                int x = (int) location.x;
                int y = (int) location.y;
                if (_homeActivity.getDesktop().addItemToPoint(item, x, y)) {
                    _homeActivity.getDesktop().consumeRevert();
                    _homeActivity.getDock().consumeRevert();
                    // add the item to the database
                    HomeActivity._db.saveItem(item, _homeActivity.getDesktop().getCurrentItem(), Definitions.ItemPosition.Desktop);
                } else {
                    Point pos = new Point();
                    _homeActivity.getDesktop().getCurrentPage().touchPosToCoordinate(pos, x, y, item._spanX, item._spanY, false);
                    View itemView = _homeActivity.getDesktop().getCurrentPage().coordinateToChildView(pos);
                    if (itemView != null && Desktop.handleOnDropOver(_homeActivity, item, (Item) itemView.getTag(), itemView, _homeActivity.getDesktop().getCurrentPage(), _homeActivity.getDesktop().getCurrentItem(), Definitions.ItemPosition.Desktop, _homeActivity.getDesktop())) {
                        _homeActivity.getDesktop().consumeRevert();
                        _homeActivity.getDock().consumeRevert();
                    } else {
                        Tool.toast(_homeActivity, R.string.toast_not_enough_space);
                        _homeActivity.getDesktop().revertLastItem();
                        _homeActivity.getDock().revertLastItem();
                    }
                }
            }

            @Override
            public void onMove(Action action, PointF location) {
                if (action != DragAction.Action.SEARCH) {
                    _homeActivity.getDesktop().updateIconProjection((int) location.x, (int) location.y);
                }
            }

            @Override
            public void onExit(Action action, PointF location) {
                for (CellContainer page : _homeActivity.getDesktop().getPages()) {
                    page.clearCachedOutlineBitmap();
                }
                dragNDropView.cancelFolderPreview();
            }

            @Override
            public void onEnd() {
                for (CellContainer page : _homeActivity.getDesktop().getPages()) {
                    page.clearCachedOutlineBitmap();
                }
                if (Setup.appSettings().isDesktopShowGrid()) {
                    _homeActivity.getDock().setHideGrid(true);
                    for (CellContainer cellContainer : _homeActivity.getDesktop().getPages()) {
                        cellContainer.setHideGrid(true);
                    }
                }
            }
        });

        // dock drag event
        dragNDropView.registerDropTarget(new DropTargetListener() {
            @Override
            public View getView() {
                return _homeActivity.getDock();
            }

            @Override
            public boolean onStart(Action action, PointF location, boolean isInside) {
                return true;
            }

            @Override
            public void onStartDrag(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onDrop(Action action, PointF location, Item item) {
                if (DragAction.Action.DRAWER.equals(action)) {
                    if (_homeActivity.getAppDrawerController()._isOpen) {
                        return;
                    }
                    item.reset();
                }

                int x = (int) location.x;
                int y = (int) location.y;
                if (_homeActivity.getDock().addItemToPoint(item, x, y)) {
                    _homeActivity.getDesktop().consumeRevert();
                    _homeActivity.getDock().consumeRevert();

                    // add the item to the database
                    HomeActivity._db.saveItem(item, 0, Definitions.ItemPosition.Dock);
                } else {
                    Point pos = new Point();
                    _homeActivity.getDock().touchPosToCoordinate(pos, x, y, item._spanX, item._spanY, false);
                    View itemView = _homeActivity.getDock().coordinateToChildView(pos);
                    if (itemView != null) {
                        if (Desktop.handleOnDropOver(_homeActivity, item, (Item) itemView.getTag(), itemView, _homeActivity.getDock(), 0, Definitions.ItemPosition.Dock, _homeActivity.getDock())) {
                            _homeActivity.getDesktop().consumeRevert();
                            _homeActivity.getDock().consumeRevert();
                        } else {
                            Tool.toast(_homeActivity, R.string.toast_not_enough_space);
                            _homeActivity.getDesktop().revertLastItem();
                            _homeActivity.getDock().revertLastItem();
                        }
                    } else {
                        Tool.toast(_homeActivity, R.string.toast_not_enough_space);
                        _homeActivity.getDesktop().revertLastItem();
                        _homeActivity.getDock().revertLastItem();
                    }
                }
            }

            @Override
            public void onMove(Action action, PointF location) {
                if (action != DragAction.Action.SEARCH) {
                    _homeActivity.getDock().updateIconProjection((int) location.x, (int) location.y);
                }
            }

            @Override
            public void onEnter(Action action, PointF location) {
                // do nothing
            }

            @Override
            public void onExit(Action action, PointF location) {
                _homeActivity.getDock().clearCachedOutlineBitmap();
                dragNDropView.cancelFolderPreview();
            }

            @Override
            public void onEnd() {
                _homeActivity.getDock().clearCachedOutlineBitmap();
            }
        });
    }

    void showItemPopup(@NonNull final ItemOptionView dragNDropView, final HomeActivity homeActivity) {
        ArrayList<PopupIconLabelItem> itemList = new ArrayList<>();
        switch (dragNDropView.getDragItem().getType()) {
            case APP:
            case SHORTCUT:
            case GROUP: {
                if (dragNDropView.getDragAction().equals(Action.DRAWER)) {
                    itemList.add(uninstallItem);
                    itemList.add(infoItem);
                } else {
                    itemList.add(editItem);
                    itemList.add(removeItem);
                    itemList.add(infoItem);
                }
                break;
            }
            case ACTION: {
                itemList.add(editItem);
                itemList.add(removeItem);
                break;
            }
            case WIDGET: {
                itemList.add(removeItem);
                break;
            }
        }

        float x = dragNDropView.getDragLocation().x - HomeActivity._itemTouchX + Tool.dp2px(10);
        float y = dragNDropView.getDragLocation().y - HomeActivity._itemTouchY - Tool.dp2px((46 * itemList.size()));

        if ((x + Tool.dp2px(200)) > dragNDropView.getWidth()) {
            dragNDropView.setPopupMenuShowDirection(false);
            x = dragNDropView.getDragLocation().x - HomeActivity._itemTouchX + homeActivity.getDesktop().getCurrentPage().getCellWidth() - Tool.dp2px(200) - Tool.dp2px(10);
        } else {
            dragNDropView.setPopupMenuShowDirection(true);
        }

        if (y < 0)
            y = dragNDropView.getDragLocation().y - HomeActivity._itemTouchY + homeActivity.getDesktop().getCurrentPage().getCellHeight() + Tool.dp2px(4);
        else
            y -= Tool.dp2px(4);

        dragNDropView.showPopupMenuForItem(x, y, itemList, new OnClickListener<PopupIconLabelItem>() {
            @Override
            public boolean onClick(View v, IAdapter<PopupIconLabelItem> adapter, PopupIconLabelItem item, int position) {
                Item dragItem;
                if ((dragItem = dragNDropView.getDragItem()) != null) {
                    switch ((int) item.getIdentifier()) {
                        case uninstallItemIdentifier: {
                            homeActivity.onUninstallItem(dragItem);
                            break;
                        }
                        case editItemIdentifier: {
                            new HpAppEditApplier(homeActivity).onEditItem(dragItem);
                            break;
                        }
                        case removeItemIdentifier: {
                            homeActivity.onRemoveItem(dragItem);
                            break;
                        }
                        case infoItemIdentifier: {
                            homeActivity.onInfoItem(dragItem);
                            break;
                        }
                    }
                }
                dragNDropView.collapse();
                return true;
            }
        });
    }
}
