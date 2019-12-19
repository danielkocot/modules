package com.reedelk.file.component;

import com.reedelk.file.commons.MimeTypeParser;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.api.message.*;
import com.reedelk.runtime.api.message.content.ByteArrayContent;
import com.reedelk.runtime.api.message.content.MimeType;
import com.reedelk.runtime.api.message.content.TypedContent;
import com.reedelk.runtime.api.resource.ResourceDynamic;
import com.reedelk.runtime.api.resource.ResourceService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.FileNotFoundException;

import static com.reedelk.file.localread.LocalFileReadAttribute.FILE_NAME;
import static com.reedelk.file.localread.LocalFileReadAttribute.TIMESTAMP;
import static com.reedelk.runtime.api.commons.ImmutableMap.of;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ESBComponent("Local file read")
@Component(service = LocalFileRead.class, scope = PROTOTYPE)
public class LocalFileRead implements ProcessorSync {

    @Reference
    private ResourceService resourceService;

    @Property("Resource file")
    private ResourceDynamic resourceFile;

    @Property("Auto mime type")
    @Default("true")
    private boolean autoMimeType;

    @Property("Mime type")
    @MimeTypeCombo
    @Default(MimeType.MIME_TYPE_TEXT_PLAIN)
    @When(propertyName = "autoMimeType", propertyValue = "false")
    @When(propertyName = "autoMimeType", propertyValue = When.BLANK)
    private String mimeType;

    @Override
    public Message apply(Message message, FlowContext flowContext) {

        try {

            ResourceService.ResourceFile resourceFile =
                    resourceService.findResourceBy(this.resourceFile, flowContext, message);

            String resourceFilePath = resourceFile.filePath();

            MimeType actualMimeType = MimeTypeParser.from(autoMimeType, mimeType, resourceFilePath);

            TypedContent<byte[]> content = new ByteArrayContent(resourceFile.data(), actualMimeType);

            MessageAttributes attributes = new DefaultMessageAttributes(LocalFileRead.class,
                    of(FILE_NAME, resourceFilePath, TIMESTAMP, System.currentTimeMillis()));

            return MessageBuilder.get().attributes(attributes).typedContent(content).build();

        } catch (FileNotFoundException e) {
            throw new ESBException(e);
        }
    }

    public void setResourceFile(ResourceDynamic resourceFile) {
        this.resourceFile = resourceFile;
    }

    public void setAutoMimeType(boolean autoMimeType) {
        this.autoMimeType = autoMimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
