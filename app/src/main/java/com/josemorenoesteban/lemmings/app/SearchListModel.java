package com.josemorenoesteban.lemmings.app;

import com.josemorenoesteban.lemmings.climber.client.v1.Climber;

import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

public class SearchListModel extends AbstractListModel<String> implements ListModel<String>  {
    private final Climber service;
    private List<String>  data;
    
    public SearchListModel() {
        this.service = Climber.of();    // TODO here we can put a default fallback
        this.data    = Collections.emptyList();
    }
    
    public void loadData(final String text) {
        this.data = service.question(text).invoke();
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
