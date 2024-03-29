package com.social.moinda.api.upload.service.provider;

import com.social.moinda.api.upload.exception.EmptyUploadFileException;
import com.social.moinda.api.upload.exception.EmptyUploadFileNameException;
import com.social.moinda.api.upload.exception.NotAllowFileExtensionException;
import com.social.moinda.core.domains.upload.constants.FileExtension;
import com.social.moinda.core.domains.upload.dto.UploadResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class AbstractUploadFileStoreProvider implements UploadFileStoreProvider {

    @Value("${spring.servlet.multipart.location}")
    private String prefixPath;

    private static final String DIRECTORY_DATE_FORMAT_PATTERN = "yyyyMMdd";

    @Override
    public List<UploadResponse> uploadFile(MultipartFile[] multipartFiles) {

        Date now = new Date();

        String formattedDate = getFormattedDate(now);

        doUploadFileValidation(multipartFiles);

        String directoryPath = makeDirectory(prefixPath, formattedDate);

        List<UploadResponse> uploadResponses = executeUpload(multipartFiles, directoryPath);

        return uploadResponses;
    }

    public abstract List<UploadResponse> executeUpload(MultipartFile[] multipartFiles, String directoryPath);

    public abstract String makeDirectory(String... fragments);

    private void doUploadFileValidation(MultipartFile[] multipartFiles) {
        for (MultipartFile multipartFile : multipartFiles) {

            String originalFilename = multipartFile.getOriginalFilename();

            if(originalFilename == null) {
                throw new EmptyUploadFileException();
            }

            int separatorFileNameAndExtensionIndex = originalFilename.lastIndexOf(".");

            String fileExtension = originalFilename.substring(separatorFileNameAndExtensionIndex + 1);

            checkFileExtension(fileExtension);

            String fileName = originalFilename.substring(0, separatorFileNameAndExtensionIndex);

            if(fileName.isEmpty()) {
                throw new EmptyUploadFileNameException();
            }
        }
    }

    public final String combineDirectoryPath(String... fragments) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String fragment : fragments) {
            stringBuilder.append(fragment);
        }
        return stringBuilder.toString();
    }

    private void checkFileExtension(String fileExtension) {
        if(!FileExtension.isValidExtension(fileExtension)) {
            throw new NotAllowFileExtensionException();
        }
    }

    private String getFormattedDate(Date now) {
        SimpleDateFormat format = new SimpleDateFormat(DIRECTORY_DATE_FORMAT_PATTERN);
        return format.format(now);
    }

}
