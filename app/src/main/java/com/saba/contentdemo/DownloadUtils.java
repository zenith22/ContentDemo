package com.saba.contentdemo;

/**
 * Created by ainamdar on 7/12/17.
 */

public class DownloadUtils{

    private static DownloadUtils downloadUtils;

    public static DownloadUtils getInstance(){
        if(downloadUtils == null){
            downloadUtils = new DownloadUtils();
        }
        return downloadUtils;
    }

    /**
     * Azhar
     * Method wll check if url is a file link, if yes it'll try to download(mp3,mp4,3gp,pdf) files
     * if it belongs to office format then will redirect to external browser
     * @param fileUrl
     * @return true if File link
     */
    public boolean isFile(String fileUrl) {
        try {

            boolean fileFormatSupported = false;
            /*Azhar-we want to  download these file formats so if the file format does not match any of the below file formats
		    	* then Return false signifying webview that it is not a supported file*/
            if (isOfficeFileFormat(fileUrl) || isVideoFileFormat(fileUrl) || isAudioFileFormat(fileUrl)) {
                fileFormatSupported = true;
            }

            if(!fileFormatSupported){
                return false;
            }

            if(fileUrl.startsWith("file:///")){
//                AppshellConfiguration.getInstance().launchFileUrlInBrowser(Uri.parse(fileUrl));
                return true;
            }

            String fname = fileUrl.substring(fileUrl.lastIndexOf('/') + 1, fileUrl.length());
            if (fname != null && fname.length() > 50) {
                if (fname.contains("=")) {
                    String fnames[] = fname.split("=");
                    fname = fnames[fnames.length - 1];
                } else {
                    fname = fname.substring(fname.length() - 10);
                }
            }
//            DownloadFileAsync downloadFile = new DownloadFileAsync(this, fileUrl);
//            downloadFile.execute(fname, fileUrl);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private static boolean isOfficeFileFormat(String url) {
        return (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx")
                || url.endsWith(".odt") || url.endsWith(".xls") || url.endsWith(".xlsx")
                || url.endsWith(".xlr") || url.endsWith(".ods") || url.endsWith(".ppt")
                || url.endsWith(".pptx") || url.endsWith(".pps") || url.endsWith(".zip"));
    }

    private static boolean isAudioFileFormat(String url){
        return (url.endsWith(".mp3") || url.endsWith(".wav"));
    }

    private static boolean isVideoFileFormat(String url){
        return (url.endsWith(".mp4") || url.endsWith(".flv") || url.endsWith(".mkv") || url.endsWith(".3gp")
                || url.endsWith(".3g2") || url.endsWith(".mov") || url.endsWith(".mpg")
                || url.endsWith(".mpeg") || url.endsWith(".wmv") || url.endsWith(".avi"));
    }


}
