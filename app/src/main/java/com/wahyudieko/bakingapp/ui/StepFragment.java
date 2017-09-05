package com.wahyudieko.bakingapp.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.wahyudieko.bakingapp.R;
import com.wahyudieko.bakingapp.entities.Step;

import java.util.ArrayList;

/**
 * Created by EKO on 04/09/2017.
 */

public class StepFragment extends Fragment{

    public static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<Step> stepArrayList = new ArrayList<Step>();

    private TextView stepDescriptionTextView;
    private SimpleExoPlayerView stepExoPlayerView;
    private ImageView stepThumbnailImageView;

    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private SimpleExoPlayer player;
    private BandwidthMeter bandwidthMeter;
    private boolean shouldAutoPlay;

    private int resumeWindow;
    private long resumePosition;

    private static final String STATE_RESUME_WINDOW = "resume_window";
    private static final String STATE_RESUME_POSITION = "resume_position";


    private MediaSource mediaSource;

    private String stepVideoUrl = "", stepThumbnailUrl = "";

    public StepFragment() {
    }

    public static StepFragment newInstance(int position, ArrayList<Step> stepArrayList) {

        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, position);

        args.putParcelableArrayList("step_list", stepArrayList);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_step, container, false);
        stepDescriptionTextView = view.findViewById(R.id.step_description_tv);
        stepExoPlayerView = view.findViewById(R.id.exo_player_view);
        stepThumbnailImageView = view.findViewById(R.id.step_thumbnail_iv);

        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getContext(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);

        stepArrayList = getArguments().getParcelableArrayList("step_list");

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            resumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            resumePosition = savedInstanceState.getInt(STATE_RESUME_POSITION);
        }else {
            clearResumePosition();
        }

        if(stepArrayList != null){
            Step step = stepArrayList.get(getArguments().getInt(ARG_SECTION_NUMBER));
            String stepDescription = step.getDescription();
            stepDescriptionTextView.setText(stepDescription);

            stepVideoUrl = step.getVideoURL();
            stepThumbnailUrl = step.getThumbnailURL();

            if(!stepVideoUrl.equals("")){
                stepThumbnailImageView.setVisibility(View.GONE);
                stepExoPlayerView.setVisibility(View.VISIBLE);
            }else if(!stepThumbnailUrl.equals("")){
                stepExoPlayerView.setVisibility(View.GONE);
                stepThumbnailImageView.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(stepThumbnailUrl)
                        .dontAnimate()
                        .into(stepThumbnailImageView);
            }else {
                stepExoPlayerView.setVisibility(View.GONE);
                stepThumbnailImageView.setVisibility(View.GONE);
            }
        }

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(!stepVideoUrl.equals("")){
                if(player != null){
                    onResume();
                }else {
                    initializePlayer(stepVideoUrl);
                }
            }else {
                onResume();
            }
        }else {
            onPause();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_RESUME_WINDOW, resumeWindow);
        outState.putLong(STATE_RESUME_POSITION, resumePosition);
    }

    private void initializePlayer(String videoUrl){

        stepExoPlayerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
        stepExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(shouldAutoPlay);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        mediaSource = new ExtractorMediaSource(Uri.parse(videoUrl),
                mediaDataSourceFactory, extractorsFactory, null, null);
        player.prepare(mediaSource);

        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
    }

    private void releasePlayer(boolean shouldPlay) {
        if (player != null) {
            if(shouldPlay){
                shouldAutoPlay = player.getPlayWhenReady();
            }
            updateResumePosition();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            if(!stepVideoUrl.equals("")){
                initializePlayer(stepVideoUrl);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer(true);
        }
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = Math.max(0, player.getContentPosition());
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

}
