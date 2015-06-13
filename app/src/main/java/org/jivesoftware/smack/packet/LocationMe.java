package org.jivesoftware.smack.packet;
import org.jivesoftware.smack.util.StringUtils;

import java.util.*;

public class LocationMe extends Packet{
	private Type type = Type.chat;
	
	private String thread = null;
    private String language;
    private final Set<Subject> subjects = new HashSet<Subject>();
    private final Set<Body> bodies = new HashSet<Body>();
	
	public LocationMe() {
    }
	public LocationMe(String to) {
        setTo(to);
    }

	public LocationMe(String to, Type type) {
        setTo(to);
        this.type = type;
    }
	public Type getType() {
        return type;
    }
	public void setType(Type type) {    	
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.type = type;
    }
	public String getSubject() {
        return getSubject(null);
    }
	public String getSubject(String language) {
        Subject subject = getMessageSubject(language);
        return subject == null ? null : subject.subject;
    }
    
    private Subject getMessageSubject(String language) {
        language = determineLanguage(language);
        for (Subject subject : subjects) {
            if (language.equals(subject.language)) {
                return subject;
            }
        }
        return null;
    }
    public Collection<Subject> getSubjects() {
        return Collections.unmodifiableCollection(subjects);
    }
    public void setSubject(String subject) {
        if (subject == null) {
            removeSubject(""); // use empty string because #removeSubject(null) is ambiguous 
            return;
        }
        addSubject(null, subject);
    }
    public Subject addSubject(String language, String subject) {
        language = determineLanguage(language);
        Subject messageSubject = new Subject(language, subject);
        subjects.add(messageSubject);
        return messageSubject;
    }
    public boolean removeSubject(String language) {
        language = determineLanguage(language);
        for (Subject subject : subjects) {
            if (language.equals(subject.language)) {
                return subjects.remove(subject);
            }
        }
        return false;
    }
    public boolean removeSubject(Subject subject) {
        return subjects.remove(subject);
    }
    public Collection<String> getSubjectLanguages() {
        Subject defaultSubject = getMessageSubject(null);
        List<String> languages = new ArrayList<String>();
        for (Subject subject : subjects) {
            if (!subject.equals(defaultSubject)) {
                languages.add(subject.language);
            }
        }
        return Collections.unmodifiableCollection(languages);
    }
    public String getBody() {
        return getBody(null);
    }
    public String getBody(String language) {
        Body body = getMessageBody(language);
        return body == null ? null : body.message;
    }
    
    private Body getMessageBody(String language) {
        language = determineLanguage(language);
        for (Body body : bodies) {
            if (language.equals(body.language)) {
                return body;
            }
        }
        return null;
    }
    public Collection<Body> getBodies() {
        return Collections.unmodifiableCollection(bodies);
    }
    public void setBody(String body) {
        if (body == null) {
            removeBody(""); // use empty string because #removeBody(null) is ambiguous
            return;
        }
        addBody(null, body);
    }
    public Body addBody(String language, String body) {
        language = determineLanguage(language);
        Body messageBody = new Body(language, body);
        bodies.add(messageBody);
        return messageBody;
    }
    public boolean removeBody(String language) {
        language = determineLanguage(language);
        for (Body body : bodies) {
            if (language.equals(body.language)) {
                return bodies.remove(body);
            }
        }
        return false;
    }
    public boolean removeBody(Body body) {
        return bodies.remove(body);
    }
    public Collection<String> getBodyLanguages() {
        Body defaultBody = getMessageBody(null);
        List<String> languages = new ArrayList<String>();
        for (Body body : bodies) {
            if (!body.equals(defaultBody)) {
                languages.add(body.language);
            }
        }
        return Collections.unmodifiableCollection(languages);
    }
    public String getThread() {
        return thread;
    }
    public void setThread(String thread) {
        this.thread = thread;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    private String determineLanguage(String language) {
        
        // empty string is passed by #setSubject() and #setBody() and is the same as null
        language = "".equals(language) ? null : language;

        // if given language is null check if message language is set
        if (language == null && this.language != null) {
            return this.language;
        }
        else if (language == null) {
            return getDefaultLanguage();
        }
        else {
            return language;
        }
        
    }
	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		
		StringBuilder buf = new StringBuilder();
        buf.append("<location");
        if (getXmlns() != null) {
            buf.append(" xmlns=\"").append(getXmlns()).append("\"");
        }
        if (language != null) {
            buf.append(" xml:lang=\"").append(getLanguage()).append("\"");
        }
        if (getPacketID() != null) {
            buf.append(" id=\"").append(getPacketID()).append("\"");
        }
        if (getTo() != null) {
            buf.append(" to=\"").append(StringUtils.escapeForXML(getTo())).append("\"");
        }
        if (getFrom() != null) {
            buf.append(" from=\"").append(StringUtils.escapeForXML(getFrom())).append("\"");
        }
        if (type != Type.normal) {
           buf.append(" type=\"").append(type).append("\"");
          // buf.append(" type=\"").append("groupchat").append("\"");
        }
        buf.append(">");
        // Add the subject in the default language
        Subject defaultSubject = getMessageSubject(null);
        if (defaultSubject != null) {
            buf.append("<subject>").append(StringUtils.escapeForXML(defaultSubject.subject)).append("</subject>");
        }
        // Add the subject in other languages
        for (Subject subject : getSubjects()) {
            // Skip the default language
            if(subject.equals(defaultSubject))
                continue;
            buf.append("<subject xml:lang=\"").append(subject.language).append("\">");
            buf.append(StringUtils.escapeForXML(subject.subject));
            buf.append("</subject>");
        }
        // Add the body in the default language
        Body defaultBody = getMessageBody(null);
        if (defaultBody != null) {
            buf.append("<body>").append(StringUtils.escapeForXML(defaultBody.message)).append("</body>");
        }
        // Add the bodies in other languages
        for (Body body : getBodies()) {
            // Skip the default language
            if(body.equals(defaultBody))
                continue;
            buf.append("<body xml:lang=\"").append(body.getLanguage()).append("\">");
            buf.append(StringUtils.escapeForXML(body.getMessage()));
            buf.append("</body>");
        }
        if (thread != null) {
            buf.append("<thread>").append(thread).append("</thread>");
        }
        // Append the error subpacket if the message type is an error.
        if (type == Type.error) {
            XMPPError error = getError();
            if (error != null) {
                buf.append(error.toXML());
            }
        }
        // Add packet extensions, if any are defined.
        buf.append(getExtensionsXML());
        buf.append("</location>");
        System.out.println("Test outing xml" + buf.toString()); //test xml
        return buf.toString();
	}
	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationMe message = (LocationMe) o;

        if(!super.equals(message)) { return false; }
        if (bodies.size() != message.bodies.size() || !bodies.containsAll(message.bodies)) {
            return false;
        }
        if (language != null ? !language.equals(message.language) : message.language != null) {
            return false;
        }
        if (subjects.size() != message.subjects.size() || !subjects.containsAll(message.subjects)) {
            return false;
        }
        if (thread != null ? !thread.equals(message.thread) : message.thread != null) {
            return false;
        }
        return type == message.type;

    }

    public int hashCode() {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = 31 * result + subjects.hashCode();
        result = 31 * result + (thread != null ? thread.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + bodies.hashCode();
        return result;
    }
    public static class Subject {

        private String subject;
        private String language;

        private Subject(String language, String subject) {
            if (language == null) {
                throw new NullPointerException("Language cannot be null.");
            }
            if (subject == null) {
                throw new NullPointerException("Subject cannot be null.");
            }
            this.language = language;
            this.subject = subject;
        }

        /**
         * Returns the language of this message subject.
         *
         * @return the language of this message subject.
         */
        public String getLanguage() {
            return language;
        }

        /**
         * Returns the subject content.
         *
         * @return the content of the subject.
         */
        public String getSubject() {
            return subject;
        }


        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.language.hashCode();
            result = prime * result + this.subject.hashCode();
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Subject other = (Subject) obj;
            // simplified comparison because language and subject are always set
            return this.language.equals(other.language) && this.subject.equals(other.subject);
        }
        
    }

    /**
     * Represents a message body, its language and the content of the message.
     */
    public static class Body {

        private String message;
        private String language;

        private Body(String language, String message) {
            if (language == null) {
                throw new NullPointerException("Language cannot be null.");
            }
            if (message == null) {
                throw new NullPointerException("Message cannot be null.");
            }
            this.language = language;
            this.message = message;
        }

        /**
         * Returns the language of this message body.
         *
         * @return the language of this message body.
         */
        public String getLanguage() {
            return language;
        }

        /**
         * Returns the message content.
         *
         * @return the content of the message.
         */
        public String getMessage() {
            return message;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.language.hashCode();
            result = prime * result + this.message.hashCode();
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Body other = (Body) obj;
            // simplified comparison because language and message are always set
            return this.language.equals(other.language) && this.message.equals(other.message);
        }
        
    }

    /**
     * Represents the type of a message.
     */
    public enum Type {

        /**
         * (Default) a normal text message used in email like interface.
         */
        normal,

        /**
         * Typically short text message used in line-by-line chat interfaces.
         */
        chat,
        
        /**
         * Typically of interface location of whereapp by kanama
         */
        location,

        /**
         * Chat message sent to a groupchat server for group chats.
         */
        groupchat,

        /**
         * Text message to be displayed in scrolling marquee displays.
         */
        headline,

        /**
         * indicates a messaging error.
         */
        error;

        public static Type fromString(String name) {
            try {
                return Type.valueOf(name);
            }
            catch (Exception e) {
                return normal;
            }
        }

    }

}
