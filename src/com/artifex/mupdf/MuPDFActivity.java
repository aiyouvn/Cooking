package com.artifex.mupdf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

//import com.kienlt.cookingebook.BookMarksActivity;
import com.kienlt.cookingebook.ContainActivity;
import com.kienlt.cookingebook.R;
import com.kienlt.cookingebook.ScreenActivity;
import com.kienlt.cookingebook.db.Bookmarks;
import com.kienlt.cookingebook.db.PdfDatabase;
import com.kienlt.cookingebook.utils.Config;

class SearchTaskResult {
	public final String txt;
	public final int pageNumber;
	public final RectF searchBoxes[];
	static private SearchTaskResult singleton;

	SearchTaskResult(String _txt, int _pageNumber, RectF _searchBoxes[]) {
		txt = _txt;
		pageNumber = _pageNumber;
		searchBoxes = _searchBoxes;
	}

	static public SearchTaskResult get() {
		return singleton;
	}

	static public void set(SearchTaskResult r) {
		singleton = r;
	}
}

class ProgressDialogX extends ProgressDialog {
	public ProgressDialogX(Context context) {
		super(context);
	}

	private boolean mCancelled = false;

	public boolean isCancelled() {
		return mCancelled;
	}

	@Override
	public void cancel() {
		mCancelled = true;
		super.cancel();
	}

}

public class MuPDFActivity extends Activity {
	/* The core rendering instance */
	private enum LinkState {
		DEFAULT, HIGHLIGHT, INHIBIT
	};

	private final int TAP_PAGE_MARGIN = 5;
	private static final int SEARCH_PROGRESS_DELAY = 200;
	private MuPDFCore core;
	private String mFileName;
	private ReaderView mDocView;
	private View mButtonsView;
	private boolean mButtonsVisible;
	private EditText mPasswordView;
	private TextView mFilenameView;
	private SeekBar mPageSlider;
	private int mPageSliderRes;
	private TextView mPageNumberView;
	private ImageButton mSearchButton;
	private ImageButton mBookmarkButton;
//	private ImageButton mBookmarkAllButton;
	private ImageButton mCancelButton;
	private ImageButton mOutlineButton;
	private ImageButton mHomeButton;
	private RelativeLayout relativeLayout;
	private ImageButton mCategory;
	private ViewSwitcher mTopBarSwitcher;
	// XXX private ImageButton mLinkButton;
	private boolean mTopBarIsSearch;
	private ImageButton mSearchBack;
	private ImageButton mSearchFwd;
	private EditText mSearchText;
	private SafeAsyncTask<Void, Integer, SearchTaskResult> mSearchTask;
	// private SearchTaskResult mSearchTaskResult;
	private AlertDialog.Builder mAlertBuilder;
	private LinkState mLinkState = LinkState.DEFAULT;
	private final Handler mHandler = new Handler();
	private int mPosition;
	private PdfDatabase sql;

	private MuPDFCore openFile(String path) {
		int lastSlashPos = path.lastIndexOf('/');
		mFileName = new String(lastSlashPos == -1 ? path
				: path.substring(lastSlashPos + 1));
		System.out.println("Trying to open " + path);
		try {
			core = new MuPDFCore(path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}

		return core;
	}

	/** Called when the activity is first created. */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAlertBuilder = new AlertDialog.Builder(this);

		if (core == null) {
			core = (MuPDFCore) getLastNonConfigurationInstance();

			if (savedInstanceState != null
					&& savedInstanceState.containsKey("")) {
				mFileName = savedInstanceState.getString("");
			}
		}
		if (core == null) {
			Intent intent = getIntent();
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri uri = intent.getData();
				if (uri.toString().startsWith("content://media/external/file")) {
					// Handle view requests from the Transformer Prime's file
					// manager
					// Hopefully other file managers will use this same scheme,
					// if not
					// using explicit paths.
					Cursor cursor = getContentResolver().query(uri,
							new String[] { "_data" }, null, null, null);
					if (cursor.moveToFirst()) {
						uri = Uri.parse(cursor.getString(0));
					}
				}

				mPosition = intent.getIntExtra(Config.INDEX_PAGE, 0);
				core = openFile(Uri.decode(uri.getEncodedPath()));
				SearchTaskResult.set(null);
			}
			if (core != null && core.needsPassword()) {
				requestPassword(savedInstanceState);
				return;
			}
		}
		if (core == null) {
			AlertDialog alert = mAlertBuilder.create();
			alert.setTitle(R.string.open_failed);
			alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return;
		}

		createUI(savedInstanceState);
	}

	public void requestPassword(final Bundle savedInstanceState) {
		mPasswordView = new EditText(this);
		mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordView
				.setTransformationMethod(new PasswordTransformationMethod());

		AlertDialog alert = mAlertBuilder.create();
		alert.setTitle(R.string.enter_password);
		alert.setView(mPasswordView);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (core.authenticatePassword(mPasswordView.getText()
								.toString())) {
							createUI(savedInstanceState);
						} else {
							requestPassword(savedInstanceState);
						}
					}
				});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alert.show();
	}

	public void createUI(Bundle savedInstanceState) {
		if (core == null)
			return;
		// Now create the UI.
		// First create the document view making use of the ReaderView's
		// internal
		// gesture recognition
		mDocView = new ReaderView(this) {
			private boolean showButtonsDisabled;

			public boolean onSingleTapUp(MotionEvent e) {
				if (e.getX() < super.getWidth() / TAP_PAGE_MARGIN) {
					super.moveToPrevious();
				} else if (e.getX() > super.getWidth() * (TAP_PAGE_MARGIN - 1)
						/ TAP_PAGE_MARGIN) {
					super.moveToNext();
				} else if (!showButtonsDisabled) {
					int linkPage = -1;
					if (mLinkState != LinkState.INHIBIT) {
						MuPDFPageView pageView = (MuPDFPageView) mDocView
								.getDisplayedView();
						if (pageView != null) {
							// XXX linkPage = pageView.hitLinkPage(e.getX(),
							// e.getY());
						}
					}

					if (linkPage != -1) {
						mDocView.setDisplayedViewIndex(linkPage);
					} else {
						if (!mButtonsVisible) {
							showButtons();
						} else {
							hideButtons();
						}
					}
				}
				return super.onSingleTapUp(e);
			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				if (!showButtonsDisabled)
					hideButtons();

				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			public boolean onScaleBegin(ScaleGestureDetector d) {
				// Disabled showing the buttons until next touch.
				// Not sure why this is needed, but without it
				// pinch zoom can make the buttons appear
				showButtonsDisabled = true;
				return super.onScaleBegin(d);
			}

			public boolean onTouchEvent(MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
					showButtonsDisabled = false;

				return super.onTouchEvent(event);
			}

			protected void onChildSetup(int i, View v) {
				if (SearchTaskResult.get() != null
						&& SearchTaskResult.get().pageNumber == i)
					((PageView) v)
							.setSearchBoxes(SearchTaskResult.get().searchBoxes);
				else
					((PageView) v).setSearchBoxes(null);

				((PageView) v)
						.setLinkHighlighting(mLinkState == LinkState.HIGHLIGHT);
			}

			protected void onMoveToChild(int i) {
				if (core == null)
					return;
				mPageNumberView.setText(String.format("%d/%d", i + 1,
						core.countPages()));
				mPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
				mPageSlider.setProgress(i * mPageSliderRes);
				if (SearchTaskResult.get() != null
						&& SearchTaskResult.get().pageNumber != i) {
					SearchTaskResult.set(null);
					mDocView.resetupChildren();
				}
			}

			protected void onSettle(View v) {
				// When the layout has settled ask the page to render
				// in HQ
				((PageView) v).addHq();
			}

			protected void onUnsettle(View v) {
				// When something changes making the previous settled view
				// no longer appropriate, tell the page to remove HQ
				((PageView) v).removeHq();
			}

			@Override
			protected void onNotInUse(View v) {
				((PageView) v).releaseResources();
			}
		};
		mDocView.setAdapter(new MuPDFPageAdapter(this, core));

		// Make the buttons overlay, and store all its
		// controls in variables
		makeButtonsView();

		// Set up the page slider
		int smax = Math.max(core.countPages() - 1, 1);
		mPageSliderRes = ((10 + smax - 1) / smax) * 2;

		// Set the file-name text
		mFilenameView.setText(mFileName);

		// Activate the seekbar
		mPageSlider
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {
						mDocView.setDisplayedViewIndex((seekBar.getProgress() + mPageSliderRes / 2)
								/ mPageSliderRes);
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						updatePageNumView((progress + mPageSliderRes / 2)
								/ mPageSliderRes);
					}
				});

		// Activate the search-preparing button
		mSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchModeOn();
			}
		});
		// Activate the BookMark
		mBookmarkButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM HH:mm"); 
				Date date = new Date();
				
				
				
				Intent intentbookmark = getIntent();
				String name_bookmark = intentbookmark
						.getStringExtra("name_pdf");
				// int
				// id_PdfFile=Integer.parseInt(intentbookmark.getStringExtra("id_PdfFile"));
				Bundle bundle = getIntent().getExtras();
				int id_PdfFile = bundle.getInt("id_PdfFile");
				int index_bookmark = mDocView.getDisplayedViewIndex() + 1;
				Log.d("adadadadadadadadada", "" + id_PdfFile + index_bookmark);
				sql = new PdfDatabase(getBaseContext());

				ArrayList<Bookmarks> array = sql.getBookmarkbyId(id_PdfFile);
				Bookmarks bookmark = new Bookmarks();
				if (array != null) {
					int i=0;
					for (;i < array.size(); i++) {
						String name = array.get(i).getName();
						int numberBookmark = array.get(i).getNumber_bookmark();
						if (name.equals(name_bookmark)
								&& numberBookmark == index_bookmark) {
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(
									MuPDFActivity.this);
							alertDialog.setTitle("Thông Báo");
							alertDialog.setIcon(R.drawable.iconwa);
							alertDialog.setMessage("Bookmark Đã Tồn Tại !");

							alertDialog.setNegativeButton("Ok",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.cancel();

										}
									});
							
							alertDialog.show();
							i = array.size();
						}

					}
					if(i != array.size()+1){
						bookmark.setName(name_bookmark);
						bookmark.setNumber_bookmark(index_bookmark);
						bookmark.setId_ck(id_PdfFile);
						bookmark.setDatetime(dateFormat.format(date));
						Log.d("tai tai tai tai ", name_bookmark + index_bookmark+dateFormat.format(date));
						sql.insertBookmark_Pdf(bookmark);
					}
				} else {

					bookmark.setName(name_bookmark);
					bookmark.setNumber_bookmark(index_bookmark);
					bookmark.setId_ck(id_PdfFile);
					bookmark.setDatetime(dateFormat.format(date));
					sql.insertBookmark_Pdf(bookmark);
					// mBookmarkButton.setImageResource(R.drawable.mark2);
					Log.d("tai tai tai tai ", name_bookmark + index_bookmark+dateFormat.format(date));
				}

			}
		});

		// Activate the BookMark
/*		mBookmarkAllButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				
				 * Intent intentbookmark=getIntent(); String
				 * name_bookmark=intentbookmark.getStringExtra("name_pdf"); int
				 * index_bookmark= mDocView.getDisplayedViewIndex()+1; sql=new
				 * PdfDatabase(getBaseContext()); Bookmarks bookmark=new
				 * Bookmarks(); bookmark.setName(name_bookmark);
				 * bookmark.setNumber_bookmark(index_bookmark);
				 * sql.insertBookmark_Pdf(bookmark); Log.d("tai tai tai tai ",
				 * name_bookmark+index_bookmark);
				 
				Intent intent = new Intent(MuPDFActivity.this,
						BookMarksActivity.class);
				Bundle bundle = getIntent().getExtras();
				int id_PdfFile = bundle.getInt("id_PdfFile");
				intent.putExtra("posi", id_PdfFile);
				startActivity(intent);

			}
		});*/
		//
		mHomeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MuPDFActivity.this,ScreenActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		mCategory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MuPDFActivity.this,
						ContainActivity.class);
				Bundle bundle = getIntent().getExtras();
				int id_PdfFile = bundle.getInt("id_PdfFile");
				String name_bookmark = bundle
						.getString("name_pdf");
				intent.putExtra("posishow", id_PdfFile);
				intent.putExtra("name_pdf_show", name_bookmark);
				startActivity(intent);
				
			}
		});



		mCancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchModeOff();
			}
		});

		// Search invoking buttons are disabled while there is no text specified
		mSearchBack.setEnabled(false);
		mSearchFwd.setEnabled(false);

		// React to interaction with the text widget
		mSearchText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				boolean haveText = s.toString().length() > 0;
				mSearchBack.setEnabled(haveText);
				mSearchFwd.setEnabled(haveText);

				// Remove any previous search results
				if (SearchTaskResult.get() != null
						&& !mSearchText.getText().toString()
								.equals(SearchTaskResult.get().txt)) {
					SearchTaskResult.set(null);
					mDocView.resetupChildren();
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		// React to Done button on keyboard
		mSearchText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE)
							search(1);
						return false;
					}
				});

		mSearchText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER)
					search(1);
				return false;
			}
		});

		// Activate search invoking buttons
		mSearchBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				search(-1);
			}
		});
		mSearchFwd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				search(1);
			}
		});

		/*
		 * XXX mLinkButton.setOnClickListener(new View.OnClickListener() {
		 * public void onClick(View v) { switch(mLinkState) { case DEFAULT:
		 * mLinkState = LinkState.HIGHLIGHT;
		 * mLinkButton.setImageResource(R.drawable.ic_hl_link); //Inform pages
		 * of the change. mDocView.resetupChildren(); break; case HIGHLIGHT:
		 * mLinkState = LinkState.INHIBIT;
		 * mLinkButton.setImageResource(R.drawable.ic_nolink); //Inform pages of
		 * the change. mDocView.resetupChildren(); break; case INHIBIT:
		 * mLinkState = LinkState.DEFAULT;
		 * mLinkButton.setImageResource(R.drawable.ic_link); break; } } });
		 */

		if (core.hasOutline()) {
			mOutlineButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OutlineItem outline[] = core.getOutline();
					if (outline != null) {
						OutlineActivityData.get().items = outline;
						Intent intent = new Intent(MuPDFActivity.this,
								OutlineActivity.class);
						startActivityForResult(intent, 0);
					}
				}
			});
		} else {
			mOutlineButton.setVisibility(View.GONE);
		}

		// Reenstate last state if it was recorded
		//SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		// mDocView.setDisplayedViewIndex(prefs.getInt("page"+mFileName, 0));
		mDocView.setDisplayedViewIndex(mPosition - 1);

		if (savedInstanceState == null
				|| !savedInstanceState.getBoolean("ButtonsHidden", false))
			showButtons();

		if (savedInstanceState != null
				&& savedInstanceState.getBoolean("SearchMode", false))
			searchModeOn();

		// Stick the document view and the buttons overlay into a parent view
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocView);
		layout.addView(mButtonsView);
		layout.setBackgroundResource(R.drawable.tiled_background);
		// layout.setBackgroundResource(R.color.canvas);
		setContentView(layout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode >= 0)
			mDocView.setDisplayedViewIndex(resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Object onRetainNonConfigurationInstance() {
		MuPDFCore mycore = core;
		core = null;
		return mycore;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mFileName != null && mDocView != null) {
			outState.putString("FileName", mFileName);

			// Store current page in the prefs against the file name,
			// so that we can pick it up each time the file is loaded
			// Other info is needed only for screen-orientation change,
			// so it can go in the bundle
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page" + mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}

		if (!mButtonsVisible)
			outState.putBoolean("ButtonsHidden", true);

		if (mTopBarIsSearch)
			outState.putBoolean("SearchMode", true);
	}

	@Override
	protected void onPause() {
		super.onPause();

		killSearch();

		if (mFileName != null && mDocView != null) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page" + mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}
	}

	public void onDestroy() {
		if (core != null)
			core.onDestroy();
		core = null;
		super.onDestroy();
	}
	
	void hideHome()
	{
		mHomeButton.setVisibility(View.INVISIBLE);
		mCategory.setVisibility(View.INVISIBLE);
		relativeLayout.setVisibility(View.INVISIBLE);
	}
	
	void showHome()
	{
		mHomeButton.setVisibility(View.VISIBLE);
		mCategory.setVisibility(View.VISIBLE);
		relativeLayout.setVisibility(View.VISIBLE);
	}

	void showButtons() {
		hideHome();
		if (core == null)
			return;
		if (!mButtonsVisible) {
			mButtonsVisible = true;
			// Update page number text and slider
			int index = mDocView.getDisplayedViewIndex();
			updatePageNumView(index);
			mPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
			mPageSlider.setProgress(index * mPageSliderRes);
			if (mTopBarIsSearch) {
				mSearchText.requestFocus();
				showKeyboard();
			}

			Animation anim = new TranslateAnimation(0, 0,
					-mTopBarSwitcher.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mTopBarSwitcher.setVisibility(View.VISIBLE);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
				}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, mPageSlider.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPageSlider.setVisibility(View.VISIBLE);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mPageNumberView.setVisibility(View.VISIBLE);
				}
			});
			mPageSlider.startAnimation(anim);
		}
	}

	void hideButtons() {
		if (mButtonsVisible) {
			mButtonsVisible = false;
			hideKeyboard();

			Animation anim = new TranslateAnimation(0, 0, 0,
					-mTopBarSwitcher.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mTopBarSwitcher.setVisibility(View.INVISIBLE);
				}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, 0, mPageSlider.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPageNumberView.setVisibility(View.INVISIBLE);
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					mPageSlider.setVisibility(View.INVISIBLE);
				}
			});
			mPageSlider.startAnimation(anim);
		}
		showHome();
	}

	void searchModeOn() {
		hideHome();
		if (!mTopBarIsSearch) {
			mTopBarIsSearch = true;
			// Focus on EditTextWidget
			mSearchText.requestFocus();
			showKeyboard();
			mTopBarSwitcher.showNext();
		}
	}

	void searchModeOff() {
		if (mTopBarIsSearch) {
			hideButtons();
			mTopBarIsSearch = false;
			hideKeyboard();
			mTopBarSwitcher.showPrevious();
			SearchTaskResult.set(null);
			// Make the ReaderView act on the change to mSearchTaskResult
			// via overridden onChildSetup method.
			mDocView.resetupChildren();
		}
		showHome();
	}

	void updatePageNumView(int index) {
		if (core == null)
			return;
		mPageNumberView.setText(String.format("%d/%d", index + 1,
				core.countPages()));
	}

	void makeButtonsView() {
		mButtonsView = getLayoutInflater().inflate(R.layout.buttons, null);
		mFilenameView = (TextView) mButtonsView.findViewById(R.id.docNameText);
		mPageSlider = (SeekBar) mButtonsView.findViewById(R.id.pageSlider);
		mPageNumberView = (TextView) mButtonsView.findViewById(R.id.pageNumber);
		mSearchButton = (ImageButton) mButtonsView
				.findViewById(R.id.searchButton);
		mBookmarkButton = (ImageButton) mButtonsView
				.findViewById(R.id.bookmarkButton);
		/*mBookmarkAllButton = (ImageButton) mButtonsView
				.findViewById(R.id.bookmarkAllButton);*/
		mCancelButton = (ImageButton) mButtonsView.findViewById(R.id.cancel);
		mOutlineButton = (ImageButton) mButtonsView
				.findViewById(R.id.outlineButton);
		mTopBarSwitcher = (ViewSwitcher) mButtonsView
				.findViewById(R.id.switcher);
		mSearchBack = (ImageButton) mButtonsView.findViewById(R.id.searchBack);
		mSearchFwd = (ImageButton) mButtonsView
				.findViewById(R.id.searchForward);
		mSearchText = (EditText) mButtonsView.findViewById(R.id.searchText);
		
		mCategory=(ImageButton) mButtonsView.findViewById(R.id.categoryButton);
	relativeLayout=(RelativeLayout)mButtonsView.findViewById(R.id.topBar3);
		mHomeButton=(ImageButton)mButtonsView.findViewById(R.id.homeButton);
		// XXX mLinkButton =
		// (ImageButton)mButtonsView.findViewById(R.id.linkButton);
		mTopBarSwitcher.setVisibility(View.INVISIBLE);
		mPageNumberView.setVisibility(View.INVISIBLE);
		mPageSlider.setVisibility(View.INVISIBLE);
	}
	

	void showKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
			imm.showSoftInput(mSearchText, 0);
	}

	void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
			imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
	}

	void killSearch() {
		if (mSearchTask != null) {
			mSearchTask.cancel(true);
			mSearchTask = null;
		}
	}

	void search(int direction) {
		hideKeyboard();
		if (core == null)
			return;
		killSearch();

		final int increment = direction;
		final int startIndex = SearchTaskResult.get() == null ? mDocView
				.getDisplayedViewIndex() : SearchTaskResult.get().pageNumber
				+ increment;

		final ProgressDialogX progressDialog = new ProgressDialogX(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle(getString(R.string.searching_));
		progressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						killSearch();
					}
				});
		progressDialog.setMax(core.countPages());

		mSearchTask = new SafeAsyncTask<Void, Integer, SearchTaskResult>() {
			@Override
			protected SearchTaskResult doInBackground(Void... params) {
				int index = startIndex;

				while (0 <= index && index < core.countPages()
						&& !isCancelled()) {
					publishProgress(index);
					RectF searchHits[] = core.searchPage(index, mSearchText
							.getText().toString());

					if (searchHits != null && searchHits.length > 0)
						return new SearchTaskResult(mSearchText.getText()
								.toString(), index, searchHits);

					index += increment;
				}
				return null;
			}

			@Override
			protected void onPostExecute(SearchTaskResult result) {
				progressDialog.cancel();
				if (result != null) {
					// Ask the ReaderView to move to the resulting page
					mDocView.setDisplayedViewIndex(result.pageNumber);
					SearchTaskResult.set(result);
					// Make the ReaderView act on the change to
					// mSearchTaskResult
					// via overridden onChildSetup method.
					mDocView.resetupChildren();
				} else {
					mAlertBuilder
							.setTitle(SearchTaskResult.get() == null ? R.string.text_not_found
									: R.string.no_further_occurences_found);
					AlertDialog alert = mAlertBuilder.create();
					alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
							(DialogInterface.OnClickListener) null);
					alert.show();
				}
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
				progressDialog.cancel();
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				super.onProgressUpdate(values);
				progressDialog.setProgress(values[0].intValue());
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mHandler.postDelayed(new Runnable() {
					public void run() {
						if (!progressDialog.isCancelled()) {
							progressDialog.show();
							progressDialog.setProgress(startIndex);
						}
					}
				}, SEARCH_PROGRESS_DELAY);
			}
		};

		mSearchTask.safeExecute();
	}

	@Override
	public boolean onSearchRequested() {
		if (mButtonsVisible && mTopBarIsSearch) {
			hideButtons();
		} else {
			showButtons();
			searchModeOn();
		}
		return super.onSearchRequested();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mButtonsVisible && !mTopBarIsSearch) {
			hideButtons();
		} else {
			showButtons();
			searchModeOff();
		}
		return super.onPrepareOptionsMenu(menu);
	}
}
