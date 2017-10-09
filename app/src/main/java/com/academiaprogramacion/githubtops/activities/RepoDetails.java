package com.academiaprogramacion.githubtops.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.academiaprogramacion.githubtops.R;
import com.academiaprogramacion.githubtops.helpers.CircleTransform;
import com.academiaprogramacion.githubtops.helpers.GitHubServiceHelper;
import com.academiaprogramacion.githubtops.helpers.Utilities;
import com.academiaprogramacion.githubtops.models.Contributor;
import com.academiaprogramacion.githubtops.models.Issue;
import com.academiaprogramacion.githubtops.models.Repository;
import com.academiaprogramacion.githubtops.services.GitHub;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepoDetails extends ActivityWithHomeEnabled {

    private static final String REPO_IMG_URL = "IMG_URL";
    private static final String REPO_ID = "REPO_ID";
    private static final String REPO_TITLE = "REPO_TITLE";
    private static final String REPO_OWNER = "REPO_OWNER";
    private static final String REPO_DESCRIPTION = "REPO_DESCRIPTION";
    private static final String REPO_STARS = "REPO_STARS";
    private static final String REPO_LANGUAGE = "REPO_LANGUAGE";

    private static final String TAG = RepoDetails.class.getSimpleName();

    @BindView(R.id.repo_image)
    ImageView mRepoImage;
    @BindView(R.id.repo_description)
    TextView mRepoDescription;
    @BindView(R.id.tv_language_name)
    TextView mLanguageName;
    @BindView(R.id.tv_stars_count)
    TextView mStarsCount;

    @BindView(R.id.main_loading) View mViewLoading;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.main_content)
    LinearLayout mMainView;
    @BindView(R.id.error_txt_cause)
    TextView txtError;
    @BindView(R.id.error_btn_retry) Button mBtnRetry;

    Bundle bundle;
    private GitHub mGitHubApi = null;

    List<Contributor> mContributors;
    List<Issue> mIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_details);
        ButterKnife.bind(this);
        setupToolbar();
        setHomeEnabled();
        Intent intent = getIntent();
        bundle = intent.getExtras();
        setActivityTitle(bundle.getString(REPO_TITLE).toUpperCase());
        mGitHubApi = GitHubServiceHelper.getGitHubClient();
        setupRepoInformation();
        mBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupRepoInformation();
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

    private void showLoadingView() {
        this.mMainView.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        this.mViewLoading.setVisibility(View.VISIBLE);
    }

    private void showContentView() {
        this.mMainView.setVisibility(View.VISIBLE);
        this.mViewLoading.setVisibility(View.GONE);
    }

    private void showErrorView(String message) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            mViewLoading.setVisibility(View.GONE);
            mMainView.setVisibility(View.GONE);
            txtError.setText(message);
        }
    }

    /**
     * Shows a loading progress bar and fetch the contributors list
     */
    private void setupRepoInformation() {
        showLoadingView();
        setContributorsList();
    }

    /**
     * Get contributors list with github client.
     */
    public void setContributorsList(){
        callTopContributors().enqueue(new Callback<List<Contributor>>() {
            @Override
            public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {
                List<Contributor> result = response.body();
                if( result != null ) {
                    mContributors = result;
                    setupIssuesList();
                    setupContributionItems();
                } else {
                    handleError(response.code());
                }
            }
            @Override
            public void onFailure(Call<List<Contributor>> call, Throwable t) {
                showErrorView(fetchErrorMessage(t));
            }
        });
    }

    /**
     * Configure the the three most popular contributors
     */
    private void setupContributionItems() {
        LinearLayout list = (LinearLayout)findViewById(R.id.linear_list_items);
        for (Contributor c : mContributors) {
            View vi = getLayoutInflater().inflate(R.layout.row_contributor, null);
            TextView name = vi.findViewById(R.id.tv_name);
            TextView contributions = vi.findViewById(R.id.tv_contributions);
            ImageView imageView = vi.findViewById(R.id.imageview_owner);
            name.setText(c.getUserName());
            contributions.setText(getResources()
                    .getString(R.string.msg_contributions, String.valueOf(c.getContributions())));
            if (c.getAvatarUrl() != null) {
                Glide.with(getApplicationContext()).load(c.getAvatarUrl())
                        .crossFade()
                        .placeholder(R.drawable.placeholder_code)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(getApplicationContext()))
                        .into(imageView);
            }
            list.addView(vi);
        }
    }

    /**
     * Get the most recent issues with github client.
     */
    private void setupIssuesList() {
        callTopIssues().enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                List<Issue> result = response.body();
                if( result != null ) {
                    mIssues = result;
                    setupIssuesItems();
                    setupRepoDetails();
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                showErrorView(fetchErrorMessage(t));
            }
        });
    }

    /**
     * Shows an error message.
     * @param code
     */
    private void handleError(int code) {
        switch (code) {
            case 422://not contributos
                showErrorView(getResources().getString(R.string.error_no_contributors));
                break;
            case 403://maxlimit
                showErrorView(getResources().getString(R.string.error_msg_max_api_calls));
                break;
            default://unknow error
                showErrorView(getResources().getString(R.string.error_msg_unknown));
        }
    }

    /**
     * Setup the issue items
     */
    private void setupIssuesItems() {
        LinearLayout list = (LinearLayout)findViewById(R.id.linear_list_issues);
        for (Issue i: mIssues) {
            Log.d(TAG, "onResponse: " + i.toString());
            View vi = getLayoutInflater().inflate(R.layout.row_issue, null);
            TextView name = vi.findViewById(R.id.tv_name);
            TextView contributions = vi.findViewById(R.id.tv_contributions);
            ImageView imageView = vi.findViewById(R.id.imageview_owner);
            name.setText(i.getUser().getLogin());
            contributions.setText(i.getTitle());
            if (i.getUser().getAvatarUrl() != null) {
                Glide.with(getApplicationContext()).load(i.getUser().getAvatarUrl())
                        .crossFade()
                        .placeholder(R.drawable.placeholder_code)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CircleTransform(getApplicationContext()))
                        .into(imageView);
            }
            list.addView(vi);
        }
    }

    /**
     * Removes the loading view and show the repo details information.
     */
    private void setupRepoDetails(){
        if (bundle.getString(REPO_IMG_URL) != null) {
            Glide.with(getApplicationContext()).load(bundle.getString(REPO_IMG_URL))
                    .crossFade()
                    .placeholder(R.drawable.placeholder_code)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new CircleTransform(getApplicationContext()))
                    .into(mRepoImage);
        }
        mRepoDescription.setText(bundle.getString(REPO_DESCRIPTION));
        mStarsCount.setText(bundle.getString(REPO_STARS));
        mLanguageName.setText(bundle.getString(REPO_LANGUAGE));
        showContentView();
    }

    /**
     * Performs a Retrofit call get the contributors list.
     * @return
     */
    private Call<List<Contributor>> callTopContributors() {
        Log.d(TAG, "callTopContributors1: " + bundle.getString(REPO_OWNER));
        Log.d(TAG, "callTopContributors2: " + bundle.getString(REPO_TITLE));
        return mGitHubApi.contributors(bundle.getString(REPO_OWNER), bundle.getString(REPO_TITLE));
    }

    private Call<List<Issue>> callTopIssues() {
        Log.d(TAG, "callTopContributors1: " + bundle.getString(REPO_OWNER));
        Log.d(TAG, "callTopContributors2: " + bundle.getString(REPO_TITLE));
        return mGitHubApi.issues(bundle.getString(REPO_OWNER), bundle.getString(REPO_TITLE));
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);
        if (!Utilities.isNetworkConnected(getApplicationContext())) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }
        return errorMsg;
    }

    public static Intent getIntentInstance(Context context, Repository repository) {
        Intent intent = new Intent(context, RepoDetails.class);
        Bundle b = new Bundle();
        b.putString(REPO_ID, String.valueOf(repository.getId()));
        b.putString(REPO_IMG_URL, repository.getOwner().getAvatarUrl());
        b.putString(REPO_TITLE, repository.getName());
        b.putString(REPO_OWNER, repository.getOwner().getLogin());
        b.putString(REPO_DESCRIPTION, repository.getDescription());
        b.putString(REPO_STARS, String.valueOf(repository.getStargazersCount()));
        b.putString(REPO_LANGUAGE, repository.getLanguage());
        intent.putExtras(b);
        return intent;
    }

}
