package reghzy.breezeui.core;

import reghzy.breezeui.Application;
import reghzy.breezeui.core.utils.Rect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class ContextLayoutManager {
    private final HashSet<UIElement> arrangeList;
    private final HashSet<UIElement> renderList;
    private boolean isInactive;


    public ContextLayoutManager() {
        this.arrangeList = new HashSet<UIElement>();
        this.renderList = new HashSet<UIElement>();
    }

    // public static ArrayList<UIElement> orderByTreeIndex(Collection<UIElement> list) {
    //
    //     LinkedHashSet<UIElement> list = new LinkedHashSet<UIElement>();
    //     list.stream().sorted(new Comparator<UIElement>() {
    //         @Override
    //         public int compare(UIElement a, UIElement b) {
    //             return 0;
    //         }
    //     }).forEach(list::add);
    // }

    public HashSet<UIElement> getRearrangeQueue() {
        return this.arrangeList;
    }

    public HashSet<UIElement> getRenderQueue() {
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
        if (Application.current().getMainWindow().isLayoutDirty) {
            Application.current().getMainWindow().updateLayout();
            return;
        }

        Rect windowRect = Application.current().getMainWindow().layoutRect;
        ArrayList<UIElement> arrange = getTopLevelComponents(this.getRearrangeQueue());
        for (UIElement element : arrange) {
            if (element.isUpdatingLayout) {
                continue;
            }

            UIElement parent = element.getParent();
            if (parent == null) {
                element.measure(Application.current().getMainWindow().layoutRect);
            }
            else {
                element.measure(parent.layoutRect);
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
