package idealab.api.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.annotation.Resource;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.GetSharedLinksResult;
import com.dropbox.core.v2.sharing.ListSharedLinksBuilder;
import com.dropbox.core.v2.sharing.ListSharedLinksErrorException;
import com.dropbox.core.v2.sharing.ListSharedLinksResult;
import com.dropbox.core.v2.sharing.SharedLinkErrorException;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api")
public class DropboxController {
  @Value("${dropbox.ACCESS_TOKEN}")
  private String ACCESS_TOKEN;

  @GetMapping("dropbox")
  ResponseEntity<?> getDropboxFileList() {
    // Create Dropbox client
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

    System.out.println("---- List Files and Folders ----");
    ListFolderResult result;
    try {
      // This current implementation lists both folders and files together
      result = client.files().listFolder("");
    } catch (DbxException e) {
      return new ResponseEntity<>("Unable to return dropbox files", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    HashMap<Number, String> dropboxFiles = new HashMap<>();
    while (true) {
      for (Metadata metadata : result.getEntries()) {
        System.out.println(metadata.getPathLower());
        dropboxFiles.put(metadata.hashCode(), metadata.getPathLower());
      }

      if (!result.getHasMore()) {
        break;
      }

      try {
        result = client.files().listFolderContinue(result.getCursor());
      } catch (DbxException e) {
        return new ResponseEntity<>("Unable to return dropbox files", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    System.out.println(dropboxFiles);
    return new ResponseEntity<>(dropboxFiles, HttpStatus.OK);
  }

  @GetMapping("/dropbox/share/{filename:.+}")
  @ResponseBody
  public ResponseEntity<?> getSharableLink(@PathVariable String filename) {
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

    ListSharedLinksResult listSharedLinksResult;
    try {
      // Gets current shared links
      listSharedLinksResult = client.sharing().listSharedLinksBuilder().withPath("/" + filename).withDirectOnly(true)
          .start();
      if (listSharedLinksResult.getLinks().isEmpty()) {
        // If no shared link exists create one
        SharedLinkMetadata sharedLinkMetadata = client.sharing().createSharedLinkWithSettings("/" + filename);
        listSharedLinksResult = client.sharing().listSharedLinksBuilder().withPath("/" + filename).withDirectOnly(true)
            .start();
      }
      return new ResponseEntity<>(listSharedLinksResult.getLinks(), HttpStatus.OK);
    } catch (DbxException e) {
      return new ResponseEntity<>("Could not get shared link <p/>" + e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/dropbox/download/{filename:.+}")
  @ResponseBody
  public ResponseEntity<?> downloadFile(@PathVariable String filename) {
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

    try {
      File file = new File("temp_files/" + filename);
      FileOutputStream out = new FileOutputStream(file);
      DbxDownloader<FileMetadata> downloader = client.files().download("/" + filename);
      downloader.download(out);

      FileInputStream fis = new FileInputStream(file);

      byte[] b = new byte[256];
      int i=0;
      String result = "";
      while ((i = fis.read(b)) != -1) {
        result += new String(b);
        System.out.print(new String(b));
      }
      fis.close();
      file.delete();

      return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
          .body(result);
    } catch (DbxException | IOException e) {
      return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("dropbox")
  ResponseEntity<?> uploadDropboxFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    // Create Dropbox client
    DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
    DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

    InputStream in;
    try {
      in = new BufferedInputStream(file.getInputStream());
    } catch (IOException e) {
      return new ResponseEntity<>("Could not upload file <p/>" + e.toString(), HttpStatus.CONFLICT);
    }

    // TODO See if file already exists.
    // If file has same content (hash checked) it will be successful, but different
    // content with same name will fail
    FileMetadata metadata;
    try {
      // TODO getOriginalFilename may return path. Test with opera?
      String dropboxFilePath = "/" + file.getOriginalFilename();
      metadata = client.files().uploadBuilder(dropboxFilePath).uploadAndFinish(in);
      return new ResponseEntity<>("Uploaded file <p/>" + metadata.toStringMultiline(), HttpStatus.OK);
    } catch (DbxException | IOException e) {
      return new ResponseEntity<>("Could not upload file <p/>" + e.toString(), HttpStatus.CONFLICT);
    }
  }
}