package com.academiaprogramacion.githubtops.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.academiaprogramacion.githubtops.R;
import com.academiaprogramacion.githubtops.activities.RepoDetails;
import com.academiaprogramacion.githubtops.helpers.CircleTransform;
import com.academiaprogramacion.githubtops.models.Repository;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Repository recyclerview adapter
 */
public class RepositoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder > implements View.OnClickListener{

    private static final String TAG = RepositoryAdapter.class.getSimpleName();
    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    // Loader status
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_END = 3;

    private final PaginationAdapterCallback mCallback;
    private List<Repository> mRepositories;
    private Context mContext;
    private boolean isLoadingAdded;
    private int mStatusFooterItem = STATUS_LOADING;
    private String errorMsg;

    public RepositoryAdapter(Context context) {
        this.mContext = context;
        this.mRepositories = new ArrayList<Repository>();
        this.mCallback = (PaginationAdapterCallback) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.row_repository, viewGroup, false);
                viewHolder = new RepositoryViewHolder(viewItem);
                viewHolder.itemView.setOnClickListener(this);
                viewHolder.itemView.setTag(viewHolder);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, viewGroup, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ITEM:
                RepositoryViewHolder repoHolder = (RepositoryViewHolder) holder;
                Repository repository = mRepositories.get(position);

                String fullName = "<b>" + repository.getOwner().getLogin() + "&#47;" + "</b>" + repository.getName();
                repoHolder.textViewFullName.setText(Html.fromHtml(fullName));
                repoHolder.textViewDescription.setText(repository.getDescription());
                String language = repository.getLanguage();
                int numStars = repository.getStargazersCount();
                repoHolder.tvLanguageName.setText(language != null ? language : "<?>");
                repoHolder.tvStarsCount.setText(String.valueOf(numStars));
                if (repository.getOwner().getAvatarUrl() != null) {
                    Glide.with(mContext).load(repository.getOwner().getAvatarUrl())
                            .crossFade()
                            .placeholder(R.drawable.placeholder_code)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new CircleTransform(mContext))
                            .into(repoHolder.imageViewOwner);
                }

                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                switch (mStatusFooterItem) {
                    case STATUS_LOADING:
                        loadingVH.showLoadingStatus();
                        break;
                    case STATUS_END:
                        loadingVH.showEndStatus();
                        loadingVH.mEndMessage.setText(errorMsg);
                        break;
                    default:
                        loadingVH.showErrorStatus();
                        loadingVH.mErrorTxt.setText(
                                errorMsg != null ?
                                        errorMsg :
                                        mContext.getString(R.string.error_msg_unknown));
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: on clice event");
        RepositoryViewHolder holder = (RepositoryViewHolder) view.getTag();
        int position = holder.getAdapterPosition();
        Intent intent = RepoDetails.getIntentInstance(mContext, mRepositories.get(position));
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return (mRepositories != null) ? mRepositories.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mRepositories.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addItems(List<Repository> elements) {
        this.mRepositories.addAll(elements);
    }

    public void add(Repository r) {
        mRepositories.add(r);
        notifyItemInserted(mRepositories.size() - 1);
    }

    public void addAll(List<Repository> repositories) {
        for (Repository repo : repositories) {
            add(repo);
        }
    }

    public void remove(Repository r) {
        int position = mRepositories.indexOf(r);
        if (position > -1) {
            mRepositories.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    /**
     * Set the loading item at the end of list
     */
    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Repository());
    }

    /**
     * Removes the loading item from the list.
     */
    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = mRepositories.size() - 1;
        Repository loadingElement = getItem(position);
        if (loadingElement != null) {
            mRepositories.remove(position);
            notifyItemRemoved(position);
        }
    }

    private Repository getItem(int position) {
        return mRepositories.get(position);
    }

    /**
     * Change the loading item status.
     * @param footerStatus: RepositoryAdapter.STATUS_LOADING, RepositoryAdapter.STATUS_END,
     *                    RepositoryAdapter.STATUS_ERROR,
     * @param errorMsg
     */
    public void setFooterItemStatus(int footerStatus, @Nullable String errorMsg) {
        this.mStatusFooterItem = footerStatus;
        if (errorMsg != null) this.errorMsg = errorMsg;
        Log.d(TAG, "setFooterItemStatus: " + errorMsg);
        notifyItemChanged(mRepositories.size() - 1);
    }

//    View holders
    /**
     * Repository item view holder
     */
    class RepositoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imageview_owner)
        public ImageView imageViewOwner;
        @BindView(R.id.textview_full_name)
        public TextView textViewFullName;
        @BindView(R.id.textview_description)
        public TextView textViewDescription;
        @BindView(R.id.tv_language_name)
        public TextView tvLanguageName;
        @BindView(R.id.tv_stars_count)
        public TextView tvStarsCount;
        @BindView(R.id.repo_row)
        public LinearLayout mLinearLayout;

        RepositoryViewHolder (View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Loading item view holder
     */
    class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.loadmore_progress)
        public ProgressBar mProgressBar;
        @BindView(R.id.loadmore_retry)
        public ImageButton mRetryBtn;
        @BindView(R.id.loadmore_errortxt)
        public TextView mErrorTxt;
        @BindView(R.id.end_message)
        public TextView mEndMessage;
        @BindView(R.id.loadmore_errorlayout)
        public LinearLayout mErrorLayout;
        @BindView(R.id.end_layout)
        public LinearLayout mEndLayout;

        LoadingVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    setFooterItemStatus(STATUS_LOADING, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }

        private void showLoadingStatus() {
            mProgressBar.setVisibility(View.VISIBLE);
            mEndLayout.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.GONE);
        }

        private void showEndStatus() {
            mProgressBar.setVisibility(View.GONE);
            mEndLayout.setVisibility(View.VISIBLE);
            mErrorLayout.setVisibility(View.GONE);
        }

        private void showErrorStatus() {
            mProgressBar.setVisibility(View.GONE);
            mEndLayout.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.VISIBLE);
        }
    }
}
