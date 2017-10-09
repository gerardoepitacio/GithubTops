package com.academiaprogramacion.githubtops.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.academiaprogramacion.githubtops.R;
import com.academiaprogramacion.githubtops.adapters.PaginationAdapterCallback;
import com.academiaprogramacion.githubtops.adapters.PaginationScrollListener;
import com.academiaprogramacion.githubtops.adapters.RepositoryAdapter;
import com.academiaprogramacion.githubtops.helpers.GitHubServiceHelper;
import com.academiaprogramacion.githubtops.models.LanguageRepos;
import com.academiaprogramacion.githubtops.models.Repository;
import com.academiaprogramacion.githubtops.services.GitHub;

import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.academiaprogramacion.githubtops.helpers.GitHubServiceHelper.DEFAULT_PAGE_ITEMS;

public class SearchResult extends ActivityWithHomeEnabled implements PaginationAdapterCallback {
    private final String TAG = SearchResult.class.getSimpleName();
    private static final String TEXT_QUERY = "TEXT_QUERY";
    private String mTextSearch = "";
    private GitHub mGitHubApi = null;
    private int mCurrentPage = 1;
    private int mTotalPages = 0;

    @BindView(R.id.main_loading) View mViewLoading;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.error_layout) LinearLayout errorLayout;
    @BindView(R.id.error_txt_cause) TextView txtError;
    @BindView(R.id.error_btn_retry) Button mBtnRetry;

    private LinearLayoutManager layoutManager;
    private RepositoryAdapter adapter;
    private boolean isLoading;
    private boolean isLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        setupToolbar();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        mTextSearch = intent.getStringExtra(TEXT_QUERY);
        mGitHubApi = GitHubServiceHelper.getGitHubClient();
        setActivityTitle(mTextSearch.toUpperCase());
        showLoadingView();
        setupRecyclerView();
        loadFirstPage();

        mBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFirstPage();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerView() {
        adapter = new RepositoryAdapter(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                mCurrentPage += 1;
                loadNextPage();
            }
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    private void loadFirstPage() {
        showLoadingView();
        callTopRepoByLanguage().enqueue(new Callback<LanguageRepos>() {
            @Override
            public void onResponse(Call<LanguageRepos> call, Response<LanguageRepos> response) {
                LanguageRepos result = response.body();
                if( result != null ) {
                    showContentView();//show Rec. view.
                    mTotalPages = result.total_count / DEFAULT_PAGE_ITEMS;
                    adapter.addAll(result.items);
                    if (mCurrentPage <= mTotalPages) adapter.addLoadingFooter();
                    else isLastPage = true;
                } else {
                    switch (response.code()) {
                        case 422:
                            showErrorView(getResources().getString(R.string.error_msg_not_results, mTextSearch));
                            break;
                        case 403:
                            showErrorView(getResources().getString(R.string.error_msg_max_api_calls));
                            break;
                        default:
                            showErrorView(getResources().getString(R.string.error_msg_unknown));
                    }
                }
            }
            @Override
            public void onFailure(Call<LanguageRepos> call, Throwable t) {
                showErrorView(fetchErrorMessage(t));
            }
        });
    }

    private void loadNextPage() {
        callTopRepoByLanguage().enqueue(new Callback<LanguageRepos>() {
            @Override
            public void onResponse(Call<LanguageRepos> call, Response<LanguageRepos> response) {
                LanguageRepos result = response.body();
                if( result != null ) {
                    adapter.removeLoadingFooter();
                    isLoading = false;
                    List<Repository> results = result.items;
                    adapter.addAll(results);
                    if ( mCurrentPage != mTotalPages)
                        adapter.addLoadingFooter();
                    else
                        isLastPage = true;
                } else {
                    setFooterItemStatus(response);
                }
            }

            @Override
            public void onFailure(Call<LanguageRepos> call, Throwable t) {
                t.printStackTrace();
                adapter.setFooterItemStatus(RepositoryAdapter.STATUS_ERROR, fetchErrorMessage(t));
            }
        });
    }

    private void setFooterItemStatus(Response<LanguageRepos> response){
        switch (response.code()) {
            case 422:
                adapter.setFooterItemStatus(RepositoryAdapter.STATUS_END,
                        getResources().getString(R.string.error_msg_max_api_limit));
                break;
            case 403:
                // https://developer.github.com/v3/search/
                adapter.setFooterItemStatus(RepositoryAdapter.STATUS_ERROR,
                        getResources().getString(R.string.error_msg_max_api_calls));
                break;
            default:
                adapter.setFooterItemStatus(RepositoryAdapter.STATUS_ERROR, getResources().getString(R.string.error_msg_unknown));
                break;
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);
        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }
        return errorMsg;
    }

    /**
     * Performs a Retrofit call to the top starred repositories.
     * @return
     */
    private Call<LanguageRepos> callTopRepoByLanguage() {
        return mGitHubApi.getLanguageRepos(
                mCurrentPage,
                GitHubServiceHelper.getLanguageParameter(mTextSearch)
        );
    }

    private void showLoadingView() {
        this.mRecyclerView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        this.mViewLoading.setVisibility(View.VISIBLE);
    }

    private void showContentView() {
        this.mRecyclerView.setVisibility(View.VISIBLE);
        this.mViewLoading.setVisibility(View.GONE);
    }

    private void showErrorView(String message) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            mViewLoading.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            txtError.setText(message);
        }
    }

    private void showEmptyResultsView(){
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            mViewLoading.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            String errorMsg = getResources().getString(R.string.error_msg_not_results, this.mTextSearch);
            txtError.setText(errorMsg);
        }
    }

    public static Intent getIntentInstance(Context context, String textQuery) {
        Intent intent = new Intent(context, SearchResult.class);
        intent.putExtra(TEXT_QUERY, textQuery);
        return intent;
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    // Helpers -------------------------------------------------------------------------------------

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            this.mViewLoading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
