package net.guanjiale.lmq.my;

import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.model.cjx.activity.BaseActivity;
import com.model.cjx.activity.CropImageActivity;
import com.model.cjx.activity.ImageSelectActivity;
import com.model.cjx.dialog.ItemSelectDialog;
import com.model.cjx.util.Tools;

import net.guanjiale.lmq.CustomApplication;
import net.guanjiale.lmq.R;
import net.guanjiale.lmq.activity.AddressSelectActivity;
import net.guanjiale.lmq.util.UploadImageTool;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cjx on 2017/1/11.
 */
public class UserInfoActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    final int RESULT_IMAGE_SELECT = 102, REQUEST_IMAGE_CAPTURE = 101, REQUEST_IMAGE_CROP = 103;
    ItemSelectDialog selectDialog;
    String mCurrentPhotoPath, cropPath;
    ImageView headView;
    UploadImageTool uploadTools;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);

        setToolBar(true, null, R.string.myself_info);
        headView = (ImageView) findViewById(R.id.user_head);
        Tools.setHeadImage(this, headView, ((CustomApplication)CustomApplication.getInstance()).user.avatar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case RESULT_IMAGE_SELECT:
                if (data != null) {
                    String[] photos = data.getStringArrayExtra("photo");
                    if (photos != null) {
                        startCropIntent(photos[0]);
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                startCropIntent(mCurrentPhotoPath);
                break;
            case REQUEST_IMAGE_CROP:
                ArrayList<String> paths = new ArrayList<>();
                paths.add(cropPath);
                uploadTools.upload(new String[]{cropPath});
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteTmepDir(new File(Tools.getTempPath(this)));
    }

    // 删除缓存路径的照片
    private void deleteTmepDir(File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            // 调用自定义相机
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                if (mCurrentPhotoPath == null) {
                    mCurrentPhotoPath = Tools.getTempPath(UserInfoActivity.this) +
                            "IMG_" + System.currentTimeMillis() + ".jpg";
                }
                Uri uri;
                File file = new File(mCurrentPhotoPath);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider",
                            file);
                }else{
                    uri = Uri.fromFile(new File(mCurrentPhotoPath));
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(UserInfoActivity.this, "no system camera find", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(this, ImageSelectActivity.class);
            intent.setAction("");
            startActivityForResult(intent, RESULT_IMAGE_SELECT);
        }
        selectDialog.dismiss();
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_address_content:
                Intent intent = new Intent(this, AddressSelectActivity.class);
                intent.setAction("");
                startActivity(intent);
                break;
            case R.id.user_head_content:
                showSelectDialog();
                break;
        }

    }

    private void showSelectDialog() {
        if (selectDialog == null) {
            selectDialog = new ItemSelectDialog(this, true);
            ArrayList<String> list = new ArrayList<>();
            list.add("拍照");
            list.add("选择照片");
            selectDialog.initData(list).bindClickListener(this);
            uploadTools = new UploadImageTool(this, new UploadImageTool.UploadResult() {
                @Override
                public void onResult(String string) {
                    uploadResult(string);
                }
            });
        }
        selectDialog.show();
    }

    // 去截图界面
    private void startCropIntent(String filePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cropPath =  Tools.getTempPath(this) + "IMG_" + System.currentTimeMillis() + ".jpg";
        Intent i = new Intent(this, CropImageActivity.class);
        i.setAction(filePath);
        i.putExtra("degree", degree);
        i.putExtra("mAspectX", 1);
        i.putExtra("mAspectY", 1);
//        i.putExtra("outputX", outputX);
//        i.putExtra("outputY", outputY);
        i.putExtra("savePath", cropPath);
        startActivityForResult(i, REQUEST_IMAGE_CROP);
    }

    // 上传头像
    private void uploadResult(String string){
        ((CustomApplication)CustomApplication.getInstance()).user.avatar = string;
        Tools.setHeadImage(this, headView, string);
    }
}
