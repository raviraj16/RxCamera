# RxCamera
A Library to open Camera or Gallery using RxAndroid.


RxImagePicker myImagePicker = new RxImagePicker(getActivity());
        myImagePicker.setSelectionMode(RxConstants.CAMERA_AND_GALLERY);
        myImagePicker.setOnOperationResult(new OnOperationResult() {
            @Override
            public void onOperationResult(RxPojo pojo) {
                Toast.makeText(getActivity(), "Result received " , Toast.LENGTH_SHORT).show();
            }
        });
        myImagePicker.chooseImage();
