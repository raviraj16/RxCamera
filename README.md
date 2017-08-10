# RxCamera

A Library to open Camera or Gallery using RxAndroid in a very simple way.



Use it in your Fragment, Activity or Dialog using below code.

RxImagePicker myImagePicker = new RxImagePicker(getActivity());

myImagePicker.setSelectionMode(RxConstants.CAMERA_AND_GALLERY);

myImagePicker.setOnOperationResult(new OnOperationResult() {

@Override
public void onOperationResult(RxPojo pojo) {

//Your code here, you can get the intent using pojo.getIntent()

Toast.makeText(getActivity(), "Result received " , Toast.LENGTH_SHORT).show();

}

});

myImagePicker.chooseImage();
        
        



//  Don't Forget to add the below line in onActivityResult of your containing Activity

RxEventBus.getEventBus().sendToBus(new RxPojo(data, resultCode));
