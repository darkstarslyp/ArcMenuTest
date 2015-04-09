package com.lyp.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.Toast;
import com.lyp.app.R;

public class ArcView extends ViewGroup implements View.OnClickListener{

    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    private Position mPosition = Position.Right_Bottom;
    private Status mStarViewStatus = Status.Close;//���β˵���״̬
    private int mRadius = 0;

    private OnMenuItemClickListener onMenuItemClickListener = null;
    private View mCButton = null;

    private enum Position {
        Left_Top, Left_Bottom, Right_Top, Right_Bottom
    }

    private enum Status {
        Open, Close
    }
    private interface OnMenuItemClickListener {
         void onClick(View view ,int pos);
    };
    private void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener){
        this.onMenuItemClickListener = onMenuItemClickListener;
    }


    public ArcView(Context context) {
        super(context, null);
    }

    public ArcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcView, defStyle, 0);

        int pos = a.getInt(R.styleable.ArcView_position, POS_RIGHT_TOP);
        switch (pos) {
            case POS_LEFT_TOP:
                mPosition = Position.Left_Top;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.Left_Bottom;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.Right_Top;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.Right_Bottom;
                break;
        }
        mRadius = (int) a.getDimension(R.styleable.ArcView_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        for(int i =0;i<count;i++){
            //测量child的measure
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        if(changed){
            layoutCButton();
            int count = getChildCount();
            for(int i=0;i<count-1;i++){
                View child = getChildAt(i+1);
                child.setVisibility(View.GONE);
                int rl = (int)(mRadius*Math.sin((Math.PI/2/(count-2))*i));
                int rt = (int)(mRadius*Math.cos((Math.PI/2/(count-2))*i));
                int vwidth = child.getMeasuredWidth();
                int vheight = child.getMeasuredHeight();

                if(mPosition==Position.Right_Bottom||mPosition==Position.Left_Bottom){
                      rt = getMeasuredHeight()-rt-vheight;
                }
                if(mPosition==Position.Right_Bottom||mPosition==Position.Right_Top){
                   rl = getMeasuredWidth()-rl-vwidth;
                }
                child.layout(rl,rt,rl+vwidth,rt+vheight);
            }
        }


    }
    private void layoutCButton() {
        mCButton = getChildAt(0);
        mCButton.setOnClickListener(this);
        int l = 0;
        int t = 0;
        int cbwidth = mCButton.getMeasuredWidth();
        int cbheight = mCButton.getMeasuredHeight();
        if(mPosition==Position.Right_Top){
            l = getMeasuredWidth()-cbwidth;
        }else if(mPosition==Position.Right_Bottom){
            l = getMeasuredWidth()-cbwidth;
            t = getMeasuredHeight()-cbheight;
        }else if(mPosition==Position.Left_Bottom){
            t = getMeasuredHeight()-cbheight;
        }
        mCButton.layout(l,t,l+cbwidth,t+cbheight);
    }

    @Override
    public void onClick(View v) {

            rotateCButton(v, 0f, 360f, 300);
            toggleMenu(300);

    }
    public void toggleMenu(int duration) {
        // 为menuItem添加平移动画和旋转动画
        int count = getChildCount();
         for(int i=0;i<count-1;i++){
             final View childView = getChildAt(i+1);
             childView.setVisibility(View.VISIBLE);

             int rl = (int)(mRadius*Math.sin((Math.PI/2/(count-2))*i));
             int rt = (int)(mRadius*Math.cos((Math.PI/2/(count-2))*i));

             int xflag = 1;
             int yflag = 1;

             if(mPosition==Position.Left_Top||mPosition==Position.Left_Bottom){
                 xflag = -1;
             }
             if(mPosition==Position.Left_Top||mPosition==Position.Right_Top){
                 yflag = -1;
             }

             AnimationSet animaset = new AnimationSet(true);
             TranslateAnimation tanima = null;

             if(Status.Close==mStarViewStatus){
                 tanima = new TranslateAnimation(xflag*rl,0,yflag*rt,0);
                 childView.setFocusable(true);
                 childView.setClickable(true);
             }
             if(Status.Open==mStarViewStatus){
                 tanima = new TranslateAnimation(0,xflag*rl,0,yflag*rt);
                 childView.setFocusable(false);
                 childView.setClickable(false);
             }
             tanima.setFillAfter(true);
             tanima.setDuration(duration);
             tanima.setAnimationListener(new Animation.AnimationListener() {
                 @Override
                 public void onAnimationStart(Animation animation) {

                 }

                 @Override
                 public void onAnimationEnd(Animation animation) {


                           if(Status.Close==mStarViewStatus){
                              childView.setVisibility(View.GONE);
                           }
                 }

                 @Override
                 public void onAnimationRepeat(Animation animation) {

                 }
             });
             Animation ranima = new RotateAnimation(0,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);

             animaset.addAnimation(tanima);
             animaset.addAnimation(ranima);
             childView.startAnimation(animaset);

             final int pos = i+1;
             childView.setOnClickListener(new OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if(onMenuItemClickListener!=null){
                         onMenuItemClickListener.onClick(childView,pos);
                     }
                       onMenuItem(pos);
                       changeStatus();
                 }
             });
         }
        changeStatus();
    }
    private void onMenuItem(int pos) {
        pos = pos-1;
        int count = getChildCount();
        for(int i =0;i<count-1;i++){
              View childView = getChildAt(i+1);
              if(i==pos){
                  childView.startAnimation(scaleBigAnim());
              }else{
                  childView.startAnimation(scaleSmallAnim());
              }
        }
    }

    private Animation scaleBigAnim(){
        AnimationSet  animaset = new AnimationSet(true);

        ScaleAnimation scanima = new ScaleAnimation(1.0f,3.0f,1.0f,3.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation almation =  new AlphaAnimation(1f,0f);

        animaset.addAnimation(scanima);
        animaset.addAnimation(almation);

        animaset.setDuration(300);
        animaset.setFillAfter(true);
        return animaset;
    }
    private Animation scaleSmallAnim(){
        AnimationSet  animaset = new AnimationSet(true);

        ScaleAnimation scanima = new ScaleAnimation(1.0f,0f,1.0f,0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation almation =  new AlphaAnimation(1f,0f);

        animaset.addAnimation(scanima);
        animaset.addAnimation(almation);

        animaset.setDuration(300);
        animaset.setFillAfter(true);
        return animaset;
    }
    private void rotateCButton(View v, float star, float end, int duration) {
        RotateAnimation animation = new RotateAnimation(star,end,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        v.startAnimation(animation);
    }

    public void changeStatus(){
         mStarViewStatus = ( mStarViewStatus == Status.Close?Status.Open : Status.Close);
    }

    public boolean  isOpen(){
       return  mStarViewStatus==Status.Open;
    }
}
