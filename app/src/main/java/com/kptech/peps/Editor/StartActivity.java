package com.kptech.peps.Editor;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kptech.peps.R;
import com.kptech.peps.Editor.listmusicandmymusic.ListMusicAndMyMusicActivity;
import com.kptech.peps.Editor.listvideoandmyvideo.ListVideoAndMyAlbumActivity;
import com.kptech.peps.Editor.listvideowithmymusic.ListVideoAndMyMusicActivity;
import com.kptech.peps.Editor.phototovideo.SelectImageAndMyVideoActivity;
import com.kptech.peps.Editor.videocollage.ListCollageAndMyAlbumActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.kptech.peps.app.MyApplication;

@SuppressLint("WrongConstant")
public class StartActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    
    private LinearLayout videocutter,videocompress,videotomp3,  audiovideomixer,videomute,videojoin,
            videotoimg,videoformatchange,fastmotion,slowmotion,videocrop,videotogif,videorotate,
            videosplit,videoreverse,videocollage,audiocompress,audiojoiner,audiocutter,phototovideo,videowatermark,videoMirror;

    static final boolean a = true;
    private Toolbar toolbar;
    private ImageView mProfileImage;



    @Override public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        if (VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
        }

        setContentView( R.layout.startactivity);

        FrameLayout native_ad_containerx = findViewById(R.id.native_ad_container);
        videoMirror=findViewById(R.id.videomirror);
        videoMirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videomirror();
            }
        });

        videocutter=findViewById(R.id.videocutter);

        videocutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videocutter();
            }
        });


        videocompress=findViewById(R.id.videocompress);
        videocompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videocompress();
            }
        });


        videotomp3=findViewById(R.id.videotomp3);

        videotomp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videotomp3();
            }
        });




        audiovideomixer=findViewById(R.id.audiovideomixer);
        audiovideomixer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audiovideomixer();
            }
        });

        videomute=findViewById(R.id.videomute);
        videomute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videomute();
            }
        });
        videojoin=findViewById(R.id.videojoin);
        videojoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videojoin();
            }
        });
        videotoimg=findViewById(R.id.videotoimg);
        videotoimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videotoimg();
            }
        });
        videoformatchange=findViewById(R.id.videoformatchange);
        videoformatchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoformatchange();
            }
        });
        fastmotion=findViewById(R.id.fastmotion);
        fastmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fastmotion();
            }
        });
        slowmotion=findViewById(R.id.slowmotion);
        slowmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slowmotion();
            }
        });
        videocrop=findViewById(R.id.videocrop);
        videocrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videocrop();
            }
        });
        videotogif=findViewById(R.id.videotogif);
        videotogif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videotogif();
            }
        });
        videorotate=findViewById(R.id.videorotate);
        videorotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videorotate();
            }
        });
        videosplit=findViewById(R.id.videosplit);
        videosplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videosplit();
            }
        });
        videoreverse=findViewById(R.id.videoreverse);
        videoreverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoreverse();
            }
        });
        videocollage=findViewById(R.id.videocollage);
        videocollage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videocollage();
            }
        });
        audiocompress=findViewById(R.id.audiocompress);
        audiocompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audiocompress();
            }
        });
        audiojoiner=findViewById(R.id.audiojoiner);
        audiojoiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audiojoiner();
            }
        });
        audiocutter=findViewById(R.id.audiocutter);
        audiocutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audiocutter();
            }
        });
        phototovideo=findViewById(R.id.phototovideo);
        phototovideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phototovideo();
            }
        });
        videowatermark=findViewById(R.id.videowatermark);
        videowatermark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videowatermark();
            }
        });

       

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.mainappbar);
        mProfileImage = (ImageView) findViewById(R.id.mainbackdrop);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();
    }


    public void videocutter() {



        Helper.ModuleId = 1;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videocompress() {
        Helper.ModuleId = 2;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videotomp3() {
        Helper.ModuleId = 3;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyMusicActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }


    public void audiovideomixer() {
        Helper.ModuleId = 4;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    @SuppressLint("WrongConstant")
    public void videomute() {
        Helper.ModuleId = 5;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }


    public void videojoin() {
        Helper.ModuleId = 6;
        Intent intent = new Intent(StartActivity.this, com.kptech.peps.Editor.videojoiner.ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videotoimg() {
        Helper.ModuleId = 7;
        Intent intent = new Intent(StartActivity.this, com.kptech.peps.Editor.videotogif.ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videoformatchange() {
        Helper.ModuleId = 8;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    @SuppressLint("WrongConstant")
    public void fastmotion() {
        Helper.ModuleId = 9;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void slowmotion() {
        Helper.ModuleId = 10;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videocrop() {
        Helper.ModuleId = 11;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videotogif() {
        Helper.ModuleId = 12;
        Intent intent = new Intent(StartActivity.this, com.kptech.peps.Editor.videotogif.ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videorotate() {
        Helper.ModuleId = 13;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videomirror() {
        Helper.ModuleId = 14;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videosplit() {
        Helper.ModuleId = 15;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videoreverse() {
        Helper.ModuleId = 16;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videocollage() {
        Helper.ModuleId = 17;
        Intent intent = new Intent(StartActivity.this, ListCollageAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void audiocompress() {
        Helper.ModuleId = 18;
        Intent intent = new Intent(StartActivity.this, ListMusicAndMyMusicActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void audiojoiner() {
        Helper.ModuleId = 19;
        Intent intent = new Intent(StartActivity.this, ListMusicAndMyMusicActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void audiocutter() {
        Helper.ModuleId = 20;
        Intent intent = new Intent(StartActivity.this, ListMusicAndMyMusicActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void phototovideo() {
        Helper.ModuleId = 21;
        Intent intent = new Intent(StartActivity.this, SelectImageAndMyVideoActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    public void videowatermark() {
        Helper.ModuleId = 22;
        Intent intent = new Intent(StartActivity.this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }




    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return a;
    }

   @Override public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.shareapp) {
            StringBuilder sb = new StringBuilder();
            sb.append(Helper.share_string);
            sb.append(Helper.package_name);
            String sb2 = sb.toString();
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", sb2);
            startActivity(intent);
        } else if (menuItem.getItemId() == R.id.moreapp) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Helper.account_string)));
            } catch (ActivityNotFoundException unused) {
                Toast.makeText(getApplicationContext(), " unable to find market app", Toast.LENGTH_LONG).show();
            }
        } else if (menuItem.getItemId() == R.id.rateus) {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(Helper.package_name)));
            } catch (ActivityNotFoundException unused2) {
                Toast.makeText(getApplicationContext(), " unable to find market app", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize = 1;
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = 0;
        try {
            percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            toolbar.setTitle("");

        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;
            toolbar.setTitle("Video Editor");


        }
    }



}
