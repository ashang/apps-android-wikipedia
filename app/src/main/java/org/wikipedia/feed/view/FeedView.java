package org.wikipedia.feed.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.wikipedia.R;
import org.wikipedia.feed.model.FeedCard;
import org.wikipedia.views.AutoFitRecyclerView;
import org.wikipedia.views.DefaultViewHolder;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedView extends FrameLayout {
    @BindView(R.id.view_feed_recycler) AutoFitRecyclerView recyclerView;
    private StaggeredGridLayoutManager recyclerLayoutManager;
    private RecyclerAdapter recyclerAdapter;

    public FeedView(Context context) {
        super(context);
        init();
    }

    public FeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FeedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void set(@NonNull List<FeedCard> cards) {
        // TODO: should this class be responsible for showing a "no items in collection" view? It
        //       would be nice to show placeholder elements while it loads.
        recyclerAdapter = new RecyclerAdapter(cards);
        recyclerView.setAdapter(recyclerAdapter);
    }

    public void update() {
        recyclerAdapter.notifyDataSetChanged();
    }

    private void init() {
        inflate(getContext(), R.layout.view_feed, this);
        ButterKnife.bind(this);
        initRecycler();
    }

    private void initRecycler() {
        recyclerLayoutManager = new StaggeredGridLayoutManager(recyclerView.getColumns(),
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.callback(new RecyclerViewColumnCallback());
        set(Collections.<FeedCard>emptyList());
    }

    private class RecyclerAdapter extends Adapter<DefaultViewHolder<FeedCardView>> {
        @NonNull private final List<FeedCard> cards;

        RecyclerAdapter(@NonNull List<FeedCard> cards) {
            this.cards = cards;
        }

        @Override public DefaultViewHolder<FeedCardView> onCreateViewHolder(ViewGroup parent,
                                                                            int viewType) {
            return new DefaultViewHolder<>(new FeedCardView(getContext()));
        }

        @Override public void onBindViewHolder(DefaultViewHolder<FeedCardView> holder, int position) {
            holder.getView().update(cards.get(position));
        }

        @Override public int getItemCount() {
            return cards.size();
        }
    }

    private class RecyclerViewColumnCallback implements AutoFitRecyclerView.Callback {
        @Override public void onColumns(int columns) {
            recyclerLayoutManager.setSpanCount(columns);
        }
    }
}