package com.josemorenoesteban.lemmings.app;

import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

public class SearchListModel extends AbstractListModel<String> implements ListModel<String>  {
    private List<String> data;
    
    public SearchListModel() {
        this.data = Collections.emptyList();
    }
    
    public void setData(final List<String> data) {
        this.data = data;
        fireContentsChanged(this, 0, this.data.size());
    }
    
    @Override
    public String getElementAt(int index) {
        return data.get(index);
    }

    @Override
    public int getSize() {
        return data.size();
    }
}
