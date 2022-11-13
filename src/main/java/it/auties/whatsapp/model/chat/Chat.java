package it.auties.whatsapp.model.chat;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.auties.protobuf.base.ProtobufMessage;
import it.auties.protobuf.base.ProtobufProperty;
import it.auties.whatsapp.api.Whatsapp;
import it.auties.whatsapp.listener.Listener;
import it.auties.whatsapp.model.contact.Contact;
import it.auties.whatsapp.model.contact.ContactJid;
import it.auties.whatsapp.model.contact.ContactJidProvider;
import it.auties.whatsapp.model.contact.ContactStatus;
import it.auties.whatsapp.model.info.MessageInfo;
import it.auties.whatsapp.model.message.model.MessageCategory;
import it.auties.whatsapp.model.sync.HistorySyncMessage;
import it.auties.whatsapp.util.Clock;
import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.Accessors;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import static it.auties.protobuf.base.ProtobufType.*;

/**
 * A model class that represents a Chat.
 * A chat can be of two types: a conversation with a contact or a group.
 * This class is only a model, this means that changing its values will have no real effect on WhatsappWeb's servers.
 * This class also offers a builder, accessible using {@link Chat#builder()}.
 */
@SuppressWarnings("ALL")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
@Jacksonized
@Accessors(fluent = true)
public final class Chat implements ProtobufMessage, ContactJidProvider {
    /**
     * The non-null unique jid used to identify this chat
     */
    @ProtobufProperty(index = 1, type = STRING, implementation = ContactJid.class)
    @NonNull
    private final ContactJid jid;

    /**
     * A non-null arrayList of messages in this chat sorted chronologically
     */
    @ProtobufProperty(index = 2, type = MESSAGE, implementation = HistorySyncMessage.class, repeated = true)
    @NonNull
    @Default
    @JsonManagedReference
    private ConcurrentLinkedDeque<MessageInfo> messages = new ConcurrentLinkedDeque<>();

    /**
     * The nullable new unique jid for this Chat.
     * This field is not null when a contact changes phone number and connects their new phone number with Whatsapp.
     */
    @ProtobufProperty(index = 3, type = STRING, implementation = ContactJid.class)
    private final ContactJid newJid;

    /**
     * The nullable old jid for this Chat.
     * This field is not null when a contact changes phone number and connects their new phone number with Whatsapp.
     */
    @ProtobufProperty(index = 4, type = STRING, implementation = ContactJid.class)
    private final ContactJid oldJid;

    /**
     * The number of unread messages in this chat.
     * If this field is negative, this chat is marked as unread.
     */
    @ProtobufProperty(index = 6, type = UINT32)
    private int unreadMessagesCount;

    /**
     * The seconds in seconds before a message is automatically deleted from this chat both locally and from WhatsappWeb's servers.
     * If ephemeral messages aren't enabled, this field has a value of 0
     */
    @ProtobufProperty(index = 9, type = UINT32)
    @Default
    private ChatEphemeralTimer ephemeralMessageDuration = ChatEphemeralTimer.OFF;

    /**
     * The seconds in seconds since {@link java.time.Instant#EPOCH} when ephemeral messages were turned on.
     * If ephemeral messages aren't enabled, this field has a value of 0.
     */
    @ProtobufProperty(index = 10, type = INT64)
    private long ephemeralMessagesToggleTime;

    /**
     * The timestamp for the creation of this chat in seconds since {@link java.time.Instant#EPOCH}
     */
    @ProtobufProperty(index = 12, type = UINT64)
    private final long timestamp;

    /**
     * The non-null display name of this chat
     */
    @ProtobufProperty(index = 13, type = STRING)
    private String name;

    /**
     * This field is used to determine whether a chat was marked as being spam or not.
     */
    @ProtobufProperty(index = 15, type = BOOL)
    private boolean notSpam;

    /**
     * This field is used to determine whether a chat is archived or not.
     */
    @ProtobufProperty(index = 16, type = BOOL)
    private boolean archived;

    /**
     * The initiator of disappearing chats
     */
    @ProtobufProperty(index = 17, type = MESSAGE, implementation = ChatDisappear.class)
    private ChatDisappear disappearInitiator;

    /**
     * The token of this chat
     */
    @ProtobufProperty(index = 21, type = BYTES)
    private byte[] token;

    /**
     * The timestamp of the token of this chat
     */
    @ProtobufProperty(index = 22, type = UINT64)
    private long tokenTimestamp;

    /**
     * The public identity key of this chat
     */
    @ProtobufProperty(index = 23, type = BYTES)
    private byte[] identityKey;

    /**
     * The seconds in seconds since {@link java.time.Instant#EPOCH} when this chat was pinned to the top.
     * If the chat isn't pinned, this field has a value of 0.
     */
    @ProtobufProperty(index = 24, type = UINT32)
    private long pinned;

    /**
     * The mute status of this chat
     */
    @ProtobufProperty(index = 25, type = UINT64)
    @NonNull
    @Default
    private ChatMute mute = ChatMute.notMuted();

    /**
     * The wallpaper of this chat
     */
    @ProtobufProperty(index = 26, type = MESSAGE, implementation = ChatWallpaper.class)
    private ChatWallpaper wallpaper;

    /**
     * The type of this media visibility set for this chat
     */
    @ProtobufProperty(index = 27, type = MESSAGE, implementation = ChatMediaVisibility.class)
    @NonNull
    @Default
    private ChatMediaVisibility mediaVisibility = ChatMediaVisibility.OFF;

    /**
     * The timestamp of the sender of the token of this chat
     */
    @ProtobufProperty(index = 28, type = UINT64)
    private long tokenSenderTimestamp;

    /**
     * Whether this chat was suspended and therefore cannot be accessed anymore
     */
    @ProtobufProperty(index = 29, type = BOOL)
    private boolean suspended;

    /**
     * A map that holds the status of each participant, excluding yourself, for this chat.
     * If the chat is not a group, this map's size will range from 0 to 1.
     * Otherwise, it will range from 0 to the number of participants - 1.
     * It is important to remember that is not guaranteed that every participant will be present as a key.
     * In this case, if this chat is a group, it can be safely assumed that the user is not available.
     * Otherwise, it's recommended to use {@link Whatsapp#subscribeToPresence(ContactJidProvider)} to force Whatsapp to send updates regarding the status of the other participant.
     * It's also possible to listen for updates to a contact's presence in a group or in a conversation by implementing {@link Listener#onContactPresence}.
     * The presence that this map indicates might not line up with {@link Contact#lastKnownPresence()} if the contact is composing, recording or paused.
     * This is because a contact can be online on Whatsapp and composing, recording or paused in a specific chat.
     */
    @Default
    @NonNull
    private Map<Contact, ContactStatus> presences = new ConcurrentHashMap<>();

    /**
     * A set that hold all the jids of the participants in this chat that have received pre keys.
     * This set is only used if the chat is a group chat.
     * It's not important for anything other than message ciphering.
     */
    @Default
    @NonNull
    private Set<ContactJid> participantsPreKeys = new HashSet<>();

    /**
     * The last hash code of the object that was serialized
     */
    @Default
    private long lastHashCode = -1;

    /**
     * Constructs a chat from a jid
     *
     * @param jid the non-null jid
     * @return a non-null chat
     */
    public static Chat ofJid(@NonNull ContactJid jid) {
        return Chat.builder()
                .jid(jid)
                .build();
    }

    /**
     * Returns the name of this chat
     *
     * @return a non-null string
     */
    public String name() {
        return Objects.requireNonNullElseGet(name, () -> this.name = jid.user());
    }

    /**
     * Returns whether this chat has a name.
     * If this method returns false, it doesn't imply that {@link Chat#name()} will return null.
     *
     * @return a boolean
     */
    public boolean hasName(){
        return name != null;
    }

    /**
     * Returns a boolean to represent whether this chat is a group or not
     *
     * @return true if this chat is a group
     */
    public boolean isGroup() {
        return jid.type() == ContactJid.Type.GROUP;
    }

    /**
     * Returns a boolean to represent whether this chat is pinned or not
     *
     * @return true if this chat is pinned
     */
    public boolean isPinned() {
        return pinned != 0;
    }

    /**
     * Returns a boolean to represent whether ephemeral messages are enabled for this chat
     *
     * @return true if ephemeral messages are enabled for this chat
     */
    public boolean isEphemeral() {
        return ephemeralMessageDuration != ChatEphemeralTimer.OFF && ephemeralMessagesToggleTime != 0;
    }

    /**
     * Returns a boolean to represent whether this chat has a new jid
     *
     * @return true if this chat has a new jid
     */
    public boolean hasNewJid() {
        return newJid != null;
    }

    /**
     * Returns a boolean to represent whether this chat has unread messages
     *
     * @return true if this chat has unread messages
     */
    public boolean hasUnreadMessages() {
        return unreadMessagesCount > 0;
    }

    /**
     * Returns all the unread messages in this chat
     *
     * @return a non-null collection
     */
    public Collection<MessageInfo> unreadMessages(){
        if(!hasUnreadMessages()){
            return List.of();
        }

        var list = new ArrayList<>(messages);
        return list.subList(list.size() - unreadMessagesCount(), list.size());
    }

    /**
     * Returns an optional value containing the new jid of this chat
     *
     * @return a non-empty optional if the new jid is not null
     */
    public Optional<ContactJid> newJid() {
        return Optional.ofNullable(newJid);
    }

    /**
     * Returns an optional value containing the old jid of this chat
     *
     * @return a non-empty optional if the old jid is not null
     */
    public Optional<ContactJid> oldJid() {
        return Optional.ofNullable(newJid);
    }

    /**
     * Returns an optional value containing the disappearing status of this chat
     *
     * @return a non-empty optional if the disappearing status of this chat is not null
     */
    public Optional<ChatDisappear> disappearInitiator() {
        return Optional.ofNullable(disappearInitiator);
    }

    /**
     * Returns an optional value containing the wallpaper of this chat, if any is set
     *
     * @return a non-empty optional if this chat has a custom wallpaper
     */
    public Optional<ChatWallpaper> wallpaper() {
        return Optional.ofNullable(wallpaper);
    }

    /**
     * Returns an optional value containing the seconds this chat was pinned
     *
     * @return a non-empty optional if the chat is pinned
     */
    public Optional<ZonedDateTime> pinned() {
        return Clock.parse(pinned);
    }

    /**
     * Returns the timestamp for the creation of this chat in seconds since {@link java.time.Instant#EPOCH}
     *
     * @return a non-empty optional if this field is populated
     */
    public Optional<ZonedDateTime> timestamp() {
        return Clock.parse(timestamp);
    }

    /**
     * Returns an optional value containing the seconds in seconds since {@link java.time.Instant#EPOCH} when ephemeral messages were turned on
     *
     * @return a non-empty optional if ephemeral messages are enabled for this chat
     */
    public Optional<ZonedDateTime> ephemeralMessagesToggleTime() {
        return Clock.parse(ephemeralMessagesToggleTime);
    }

    /**
     * Returns an optional value containing the latest message in chronological terms for this chat
     *
     * @return an optional
     */
    public Optional<MessageInfo> lastMessage() {
        return messages.isEmpty() ?
                Optional.empty() :
                Optional.of(messages.getLast());
    }

    /**
     * Returns an optional value containing the latest message in chronological terms for this chat with type that isn't server
     *
     * @return an optional
     */
    public Optional<MessageInfo> lastStandardMessage() {
        return messages.stream()
                .filter(info -> !info.message()
                        .hasCategory(MessageCategory.SERVER))
                .reduce((first, second) -> second);
    }

    /**
     * Returns an optional value containing the latest message in chronological terms for this chat sent from you
     *
     * @return an optional
     */
    public Optional<MessageInfo> lastMessageFromMe() {
        return messages.stream()
                .filter(MessageInfo::fromMe)
                .reduce((first, second) -> second);
    }

    /**
     * Returns an optional value containing the latest message in chronological terms for this chat with type server
     *
     * @return an optional
     */
    public Optional<MessageInfo> lastServerMessage() {
        return messages.stream()
                .filter(info -> info.message()
                        .hasCategory(MessageCategory.SERVER))
                .reduce((first, second) -> second);
    }

    /**
     * Returns an optional value containing the first message in chronological terms for this chat
     *
     * @return an optional
     */
    public Optional<MessageInfo> firstMessage() {
        return messages.isEmpty() ?
                Optional.empty() :
                Optional.of(messages.getFirst());
    }

    /**
     * Returns an optional value containing the first message in chronological terms for this chat sent from you
     *
     * @return an optional
     */
    public Optional<MessageInfo> firstMessageFromMe() {
        return messages.stream()
                .filter(MessageInfo::fromMe)
                .findFirst();
    }

    /**
     * Returns an optional value containing the first message in chronological terms for this chat with type that isn't server
     *
     * @return an optional
     */
    public Optional<MessageInfo> firstStandardMessage() {
        return messages.stream()
                .filter(info -> !info.message()
                        .hasCategory(MessageCategory.SERVER))
                .findFirst();
    }

    /**
     * Returns an optional value containing the first message in chronological terms for this chat with type server
     *
     * @return an optional
     */
    public Optional<MessageInfo> firstServerMessage() {
        return messages.stream()
                .filter(info -> info.message()
                        .hasCategory(MessageCategory.SERVER))
                .findFirst();
    }

    /**
     * Returns all the starred messages in this chat
     *
     * @return a non-null list of messages
     */
    public List<MessageInfo> starredMessages() {
        return messages.stream()
                .filter(MessageInfo::starred)
                .toList();
    }

    /**
     * Adds a message to the chat
     *
     * @param info the message to add to the chat
     * @return whether the message was added
     */
    public boolean addMessage(@NonNull MessageInfo info) {
        return !messages.contains(info) && messages.add(info);
    }

    /**
     * Remove a message from the chat
     *
     * @param info The message to remove
     */
    public void removeMessage(@NonNull MessageInfo info) {
        messages.remove(info);
    }

    /**
     * Checks if this chat is equal to another chat
     *
     * @param other the chat
     * @return a boolean
     */
    public boolean equals(Object other){
        return other instanceof Chat that
                && Objects.equals(this.jid(), that.jid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(jid);
    }

    /**
     * Returns this object as a jid
     *
     * @return a non-null jid
     */
    @Override
    @NonNull
    public ContactJid toJid() {
        return jid();
    }

    public static class ChatBuilder {
        public ChatBuilder messages(List<MessageInfo> messages) {
            if (this.messages$value == null) {
                this.messages$value = new ConcurrentLinkedDeque<>();
                this.messages$set = true;
            }

            // Kind of abusing the type system of java
            // If the chat was received from Whatsapp, the actual type of the list is HistorySyncMessage, and it needs to be unwrapped
            // Though if the message was stored locally it's actually a MessageInfo(unwrapped HistorySyncMessage)
            // If the type of the messages parameter were to be Object though, Jackson wouldn't be able to deserialize it correctly(would be assumed as a map)
            // So we need to specify the MessageInfo type so that jackson can use a base type if it's not sure of the content of the list
            // And loop through the messages as if they were Objects because accessing it in any other way would yield a ClassCastException
            // I could switch to using a HistorySyncMessage List instead and return a List of MessageInfo through an accessor, but this is very costly as this list might be huge
            // This looks to be the best approach
            messages.forEach((Object entry) -> {
                if (entry instanceof HistorySyncMessage historySyncMessage) {
                    messages$value.add(historySyncMessage.message());
                    return;
                }

                if (entry instanceof MessageInfo messageInfo) {
                    messages$value.add(messageInfo);
                    return;
                }

                throw new IllegalArgumentException("Unexpected value: " + entry.getClass()
                        .getName());
            });

            return this;
        }
    }
}
