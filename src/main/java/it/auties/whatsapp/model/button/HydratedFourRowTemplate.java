package it.auties.whatsapp.model.button;

import com.fasterxml.jackson.annotation.JsonCreator;
import it.auties.protobuf.base.ProtobufMessage;
import it.auties.protobuf.base.ProtobufProperty;
import it.auties.whatsapp.model.message.standard.DocumentMessage;
import it.auties.whatsapp.model.message.standard.ImageMessage;
import it.auties.whatsapp.model.message.standard.LocationMessage;
import it.auties.whatsapp.model.message.standard.VideoMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.auties.protobuf.base.ProtobufType.MESSAGE;
import static it.auties.protobuf.base.ProtobufType.STRING;

/**
 * A model class that represents a hydrated four row template
 */
@AllArgsConstructor
@Data
@Builder
@Jacksonized
@Accessors(fluent = true)
public class HydratedFourRowTemplate implements ProtobufMessage {
    @ProtobufProperty(index = 1, type = MESSAGE, implementation = DocumentMessage.class)
    private DocumentMessage documentTitle;

    @ProtobufProperty(index = 2, type = STRING)
    private String textTitle;

    @ProtobufProperty(index = 3, type = MESSAGE, implementation = ImageMessage.class)
    private ImageMessage imageTitle;

    @ProtobufProperty(index = 4, type = MESSAGE, implementation = VideoMessage.class)
    private VideoMessage videoTitle;

    @ProtobufProperty(index = 5, type = MESSAGE, implementation = LocationMessage.class)
    private LocationMessage locationTitle;

    @ProtobufProperty(index = 6, type = STRING)
    private String body;

    @ProtobufProperty(index = 7, type = STRING)
    private String footer;

    @ProtobufProperty(index = 8, type = MESSAGE, implementation = HydratedButtonTemplate.class, repeated = true)
    private List<HydratedButtonTemplate> buttons;

    @ProtobufProperty(index = 9, type = STRING)
    private String id;

    /**
     * Constructs a new builder to create a four row template without a title
     *
     * @param body    the body of this template
     * @param footer  the footer of this template
     * @param buttons the buttons of this template
     * @return a non-null new template
     */
    @Builder(builderClassName = "EmptyFourRowTemplateBuilder", builderMethodName = "withoutTitleBuilder")
    private static HydratedFourRowTemplate emptyBuilder(String body, String footer,
                                                        List<HydratedButtonTemplate> buttons, String id) {
        return createBuilder(body, footer, buttons, id).build();

    }

    /**
     * Constructs a new builder to create a four row template with a document title
     *
     * @param title   the title of this template
     * @param body    the body of this template
     * @param footer  the footer of this template
     * @param buttons the buttons of this template
     * @return a non-null new template
     */
    @Builder(builderClassName = "DocumentHydratedFourRowTemplateBuilder", builderMethodName = "withDocumentTitleBuilder")
    private static HydratedFourRowTemplate documentBuilder(DocumentMessage title, String body, String footer,
                                                           List<HydratedButtonTemplate> buttons, String id) {
        return createBuilder(body, footer, buttons, id).documentTitle(title)
                .build();

    }

    /**
     * Constructs a new builder to create a four row template with a text title
     *
     * @param title   the title of this template
     * @param body    the body of this template
     * @param footer  the footer of this template
     * @param buttons the buttons of this template
     * @return a non-null new template
     */
    @Builder(builderClassName = "HighlyStructuredHydratedFourRowTemplateBuilder", builderMethodName = "withTextTitleBuilder")
    private static HydratedFourRowTemplate textBuilder(String title, String body, String footer,
                                                       List<HydratedButtonTemplate> buttons, String id) {
        return createBuilder(body, footer, buttons, id).textTitle(title)
                .build();
    }

    /**
     * Constructs a new builder to create a four row template with an image title
     *
     * @param title   the title of this template
     * @param body    the body of this template
     * @param footer  the footer of this template
     * @param buttons the buttons of this template
     * @return a non-null new template
     */
    @Builder(builderClassName = "ImageHydratedFourRowTemplateBuilder", builderMethodName = "withImageTitleBuilder")
    private static HydratedFourRowTemplate imageBuilder(ImageMessage title, String body, String footer,
                                                        List<HydratedButtonTemplate> buttons, String id) {
        return createBuilder(body, footer, buttons, id).imageTitle(title)
                .build();

    }

    /**
     * Constructs a new builder to create a four row template with a video title
     *
     * @param title   the title of this template
     * @param body    the body of this template
     * @param footer  the footer of this template
     * @param buttons the buttons of this template
     * @return a non-null new template
     */
    @Builder(builderClassName = "VideoHydratedFourRowTemplateBuilder", builderMethodName = "withVideoTitleBuilder")
    private static HydratedFourRowTemplate videoBuilder(VideoMessage title, String body, String footer,
                                                        List<HydratedButtonTemplate> buttons, String id) {
        return createBuilder(body, footer, buttons, id).videoTitle(title)
                .build();

    }

    /**
     * Constructs a new builder to create a four row template with a location title
     *
     * @param title   the title of this template
     * @param body    the body of this template
     * @param footer  the footer of this template
     * @param buttons the buttons of this template
     * @return a non-null new template
     */
    @Builder(builderClassName = "LocationHydratedFourRowTemplateBuilder", builderMethodName = "withLocationTitleBuilder")
    private static HydratedFourRowTemplate locationBuilder(LocationMessage title, String body, String footer,
                                                           List<HydratedButtonTemplate> buttons, String id) {
        return createBuilder(body, footer, buttons, id).locationTitle(title)
                .build();

    }

    private static HydratedFourRowTemplate.HydratedFourRowTemplateBuilder createBuilder(String body, String footer,
                                                                                        List<HydratedButtonTemplate> buttons,
                                                                                        String id) {
        return HydratedFourRowTemplate.builder()
                .body(body)
                .footer(footer)
                .buttons(buttons)
                .id(id);
    }

    /**
     * Returns the type of title that this template wraps
     *
     * @return a non-null title type
     */
    public TitleType titleType() {
        if (documentTitle != null)
            return TitleType.DOCUMENT_MESSAGE;
        if (textTitle != null)
            return TitleType.TEXT_TITLE;
        if (imageTitle != null)
            return TitleType.IMAGE_MESSAGE;
        if (videoTitle != null)
            return TitleType.VIDEO_MESSAGE;
        if (locationTitle != null)
            return TitleType.LOCATION_MESSAGE;
        return TitleType.NONE;
    }

    /**
     * The constants of this enumerated type describe the various types of title that a template can wrap
     */
    @AllArgsConstructor
    @Accessors(fluent = true)
    public enum TitleType implements ProtobufMessage {
        /**
         * No title
         */
        NONE(0),

        /**
         * Document title
         */
        DOCUMENT_MESSAGE(1),

        /**
         * Text title
         */
        TEXT_TITLE(2),

        /**
         * Image title
         */
        IMAGE_MESSAGE(3),

        /**
         * Video title
         */
        VIDEO_MESSAGE(4),

        /**
         * Location title
         */
        LOCATION_MESSAGE(5);

        @Getter
        private final int index;

        @JsonCreator
        public static TitleType of(int index) {
            return Arrays.stream(values())
                    .filter(entry -> entry.index() == index)
                    .findFirst()
                    .orElse(TitleType.NONE);
        }
    }

    public static class HydratedFourRowTemplateBuilder {
        public HydratedFourRowTemplateBuilder buttons(List<HydratedButtonTemplate> hydratedButtons) {
            if (this.buttons == null)
                this.buttons = new ArrayList<>();
            this.buttons.addAll(hydratedButtons);
            return this;
        }
    }
}
