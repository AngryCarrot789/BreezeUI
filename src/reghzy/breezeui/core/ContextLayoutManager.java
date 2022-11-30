package reghzy.breezeui.core;

import reghzy.breezeui.Application;
import reghzy.breezeui.core.utils.Rect;
import reghzy.breezeui.window.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

public class ContextLayoutManager {
    private final Queue<UIElement> arrangeQueue;
    private boolean isInactive;

    private final ArrayList<UIElement> renderList;

    public ContextLayoutManager() {
        this.arrangeQueue = new LinkedList<UIElement>();
        this.renderList = new ArrayList<UIElement>();
    }

    public Queue<UIElement> getRearrangeQueue() {
        return this.arrangeQueue;
    }

    public ArrayList<UIElement> getRenderList() {
        return this.renderList;
    }

    public static ContextLayoutManager of() {
        return Application.current().getCurrentLayoutManager();
    }

    // a
    //   b
    //     [c]
    //        [d]
    //        [f]
    //  [e]
    //
    //
    //
    //
    //
    //
    //

    public static ArrayList<UIElement> getTopLevelComponents(Collection<UIElement> elements) {
        LinkedHashSet<UIElement> top1 = new LinkedHashSet<UIElement>();
        LinkedHashSet<UIElement> top2 = new LinkedHashSet<UIElement>();
        HashSet<UIElement> set = new HashSet<UIElement>(elements);

        for(UIElement element : elements) {
            UIElement topLevel = element, parent = element.getParent();
            while (parent != null && set.contains(parent)) {
                topLevel = parent;
                parent = parent.getParent();
            }

            top1.add(topLevel);
        }

        for (UIElement element : top1) {
            UIElement topLevel = element, parent = element.getParent();
            while (parent != null && set.contains(parent)) {
                topLevel = parent;
                parent = parent.getParent();
            }

            top2.add(topLevel);
        }

        return new ArrayList<UIElement>(top2);
    }

    public void setInactive() {
        this.isInactive = true;
    }

    public void setActive() {
        this.isInactive = false;
    }

    public boolean isInactive() {
        return this.isInactive;
    }

    public boolean isActive() {
        return !this.isInactive;
    }

    public void updateLayout() {
        ArrayList<UIElement> arrange = getTopLevelComponents(this.getRearrangeQueue());
        for (UIElement element : arrange) {
            if (element.isUpdatingLayout) {
                continue;
            }

            if (element.parent == null) {
                element.measure(Application.current().getMainWindow().layoutRect);
            }
            else {
                element.measure(element.parent.layoutRect);
            }

            // element.arrange(element.getDesiredSize());

            // element.isArrangeInProgress = true;
            // {
            //     // Rect rect;
            //     // Thickness margin = element.getMargin();
            //     // UIElement parent = element.getParent();
            //     // if (parent == null) {
            //     //     rect = new Rect(margin.getLeft(), margin.getTop(), )
            //     // }
            //     // Rect rect = new Rect()
            //     // element.arrange();
            // }
            // element.isArrangeInProgress = false;
            // element.isArrangeDirty = false;
        }
    }
}
