package org.html5index.docscan;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.html5index.model.DocumentationProvider;

public class Sources {
	public static final String CSS_SPEC_LINKS = "/spec/css-links.properties";
	public static final String JS_SPEC_LINKS = "/spec/js-links.properties";
	
	public static DocumentationProvider[] SOURCES = {
		/*
		// css-property
		new CrosswalkCSSSpecScan("CSS3-Color", "http://www.w3.org/TR/css3-color/"),
		new CrosswalkCSSSpecScan("CSS3-Background", "http://www.w3.org/TR/css3-background/"),
		new CrosswalkCSSSpecScan("CSS3-Images", "http://www.w3.org/TR/css3-images/"),
		new CrosswalkCSSSpecScan("CSS3-Marquee", "http://www.w3.org/TR/css3-marquee/"),
		new CrosswalkCSSSpecScan("CSS3-Multicol", "http://www.w3.org/TR/css3-multicol/"),
		new CrosswalkCSSSpecScan("CSS3-Speech", "http://www.w3.org/TR/css3-speech/"),
		new CrosswalkCSSSpecScan("CSS3-Flexbox", "http://www.w3.org/TR/css3-flexbox/"),
		new CrosswalkCSSSpecScan("CSS3-Text-decor-3", "http://www.w3.org/TR/css-text-decor-3/"),
		new CrosswalkCSSSpecScan("CSS3-Animations", "http://www.w3.org/TR/css3-animations/"),
		new CrosswalkCSSSpecScan("CSS3-Webfonts", "http://www.w3.org/TR/css3-webfonts/"),
		new CrosswalkCSSSpecScan("CSS3-Variables", "http://www.w3.org/TR/css-variables/"),
		new CrosswalkCSSSpecScan("CSS3-Counter-styles-3", "http://www.w3.org/TR/css-counter-styles-3/"),
		new CrosswalkCSSSpecScan("CSS3-Text", "http://www.w3.org/TR/css3-text/"),
		new CrosswalkCSSSpecScan("CSS3-Break", "http://www.w3.org/TR/css3-break/"),
		new CrosswalkCSSSpecScan("CSS3-Transforms", "http://www.w3.org/TR/css3-transforms/"),
		new CrosswalkCSSSpecScan("CSS3-Transitions", "http://www.w3.org/TR/css3-transitions/"),
		new CrosswalkCSSSpecScan("CSS3-Writing-modes", "http://www.w3.org/TR/css3-writing-modes/"),
		new CrosswalkCSSSpecScan("CSS3-Cascade", "http://www.w3.org/TR/css3-cascade/"),
		new CrosswalkCSSSpecScan("CSS3-Page", "http://www.w3.org/TR/css3-page/"),
		new CrosswalkCSSSpecScan("CSS3-Ui", "http://www.w3.org/TR/css3-ui/"),
		
		//Responsive Design
		new CrosswalkCSSSpecScan("CSS-Device-adapt", "http://dev.w3.org/csswg/css-device-adapt/"),
		new CrosswalkCSSSpecScan("CSS-mediaqueries4", "http://dev.w3.org/csswg/mediaqueries4/"),
		
		
		// css-operator
		new Html5SpecScan("Selectors-api", "http://www.w3.org/TR/selectors-api/"),
		new Html5SpecScan("Selectors-api2", "http://www.w3.org/TR/selectors-api2/"),
		new Html5SpecScan("CSS3-Conditional", "http://www.w3.org/TR/css3-conditional"),
		new Html5SpecScan("Cssom", "http://www.w3.org/TR/cssom/"),
		new Html5SpecScan("Cssom-view", "http://www.w3.org/TR/cssom-view/"),
		new Html5SpecScan("Widgets-apis", "http://www.w3.org/TR/widgets-apis/"),
		new Html5SpecScan("Web-alarms", "http://www.w3.org/2012/sysapps/web-alarms/"),
		new Html5SpecScan("Battery", "https://dvcs.w3.org/hg/dap/raw-file/default/battery/Overview.html"),
		new Html5SpecScan("Camera", "http://dev.w3.org/2009/dap/camera/"),
		new Html5SpecScan("Light", "https://dvcs.w3.org/hg/dap/raw-file/default/light/Overview.html"),
		new Html5SpecScan("Contacts-manager-api", "http://www.w3.org/2012/sysapps/contacts-manager-api/"),
		new Html5SpecScan("Messaging", "http://www.w3.org/2012/sysapps/messaging/"),
		new Html5SpecScan("Telephony", "http://www.w3.org/2012/sysapps/telephony/"),
		new Html5SpecScan("Raw-sockets", "http://www.w3.org/2012/sysapps/raw-sockets/"),
		new Html5SpecScan("Device-capabilities", "http://www.w3.org/2012/sysapps/device-capabilities/"),
		new Html5SpecScan("Secure-element", "http://opoto.github.io/secure-element/"),
		new Html5SpecScan("Editor", "http://dev.w3.org/2011/webrtc/editor/getusermedia.html"),
		new Html5SpecScan("Media-stream-capture", "https://dvcs.w3.org/hg/dap/raw-file/default/media-stream-capture/MediaRecorder.html"),
		
		//Device APIs
		new Html5SpecScan("Proximity", "https://dvcs.w3.org/hg/dap/raw-file/default/proximity/Overview.html"),
		new Html5SpecScan("Vibration", "http://dev.w3.org/2009/dap/vibration/"),
		new Html5SpecScan("Discovery-api", "https://dvcs.w3.org/hg/dap/raw-file/default/discovery-api/Overview.html"),
		new Html5SpecScan("Geolocation-API", "http://www.w3.org/TR/geolocation-API/"),
		new Html5SpecScan("Spec-source-orientation", "http://dev.w3.org/geo/api/spec-source-orientation.html"),
		new Html5SpecScan("Fullscreen", "http://fullscreen.spec.whatwg.org/"),
		new Html5SpecScan("Touchevents", "https://dvcs.w3.org/hg/webevents/raw-file/v1/touchevents.html"),
		new Html5SpecScan("pointerEvents", "https://dvcs.w3.org/hg/pointerevents/raw-file/tip/pointerEvents.html"),
		new Html5SpecScan("gamepad", "https://dvcs.w3.org/hg/gamepad/raw-file/default/gamepad.html"),
		new Html5SpecScan("Presentation", "http://webscreens.github.io/presentation-api/"),
		new Html5SpecScan("templates", "https://dvcs.w3.org/hg/webcomponents/raw-file/tip/spec/templates/index.html"),
		new Html5SpecScan("custom", "http://w3c.github.io/webcomponents/spec/custom/"),
		new Html5SpecScan("shadow", "http://w3c.github.io/webcomponents/spec/shadow/"),
		new Html5SpecScan("imports", "http://w3c.github.io/webcomponents/spec/imports/"),
		new Html5SpecScan("DOM", "http://dom.spec.whatwg.org/"),
		new Html5SpecScan("Typed Array", "http://www.khronos.org/registry/typedarray/specs/latest/"),
		new Html5SpecScan("The picture Element", "http://picture.responsiveimages.org/"),
		new Html5SpecScan("html5", "http://www.w3.org/TR/html5/embedded-content-0.html"),
		new Html5SpecScan("WebGL", "http://www.khronos.org/registry/webgl/specs/latest/1.0/"),
		new Html5SpecScan("2dcontext", "http://www.w3.org/TR/2dcontext/"),
		new Html5SpecScan("html51", "http://www.w3.org/TR/html51/embedded-content-0.html"),
		new Html5SpecScan("web-animations", "http://www.w3.org/TR/web-animations/"),
		new Html5SpecScan("animation-timing", "http://www.w3.org/TR/animation-timing/"),
		new Html5SpecScan("encrypted-media", "http://www.w3.org/TR/encrypted-media/"),
		new Html5SpecScan("media-source", "http://www.w3.org/TR/media-source/"),
		new Html5SpecScan("browsers", "http://www.w3.org/html/wg/drafts/html/CR/browsers.html"),
		new Html5SpecScan("XMLHttpRequest", "http://www.w3.org/TR/XMLHttpRequest/"),
		new Html5SpecScan("ime-api", "https://dvcs.w3.org/hg/ime-api/raw-file/default/Overview.html"),
		new Html5SpecScan("notifications", "https://dvcs.w3.org/hg/notifications/raw-file/tip/Overview.html"),
		new Html5SpecScan("forms5", "http://www.w3.org/TR/html5/forms.html"),
		new Html5SpecScan("forms51", "http://www.w3.org/TR/html51/forms.html"),
		new Html5SpecScan("speech-api", "https://dvcs.w3.org/hg/speech-api/raw-file/tip/speechapi.html"),
		new Html5SpecScan("interactive-elements", "http://www.w3.org/html/wg/drafts/html/CR/interactive-elements.html"),
		new Html5SpecScan("indie-ui-events", "http://www.w3.org/TR/indie-ui-events/"),
		new Html5SpecScan("IndexedDB", "https://dvcs.w3.org/hg/IndexedDB/raw-file/default/Overview.html"),
		new Html5SpecScan("webstorage", "http://www.w3.org/TR/webstorage/"),
		new Html5SpecScan("browsers5", "http://www.w3.org/TR/html5/browsers.html"),
		new Html5SpecScan("FileAPI", "http://dev.w3.org/2006/webapi/FileAPI/"),
		new Html5SpecScan("File API: Directories and System", "http://dev.w3.org/2009/dap/file-system/file-dir-sys.html"),
		new Html5SpecScan("file-writer", "http://dev.w3.org/2009/dap/file-system/file-writer.html"),
		new Html5SpecScan("webdatabase", "http://www.w3.org/TR/webdatabase/"),
		new Html5SpecScan("quota-api", "http://www.w3.org/TR/quota-api/"),
		new Html5SpecScan("navigation-timing", "http://www.w3.org/TR/navigation-timing/"),
		new Html5SpecScan("resource-timing", "http://www.w3.org/TR/resource-timing/"),
		new Html5SpecScan("performance-timeline", "http://www.w3.org/TR/performance-timeline/"),
		new Html5SpecScan("user-timing", "http://www.w3.org/TR/user-timing/"),
		new Html5SpecScan("setImmediate", "http://w3c-test.org/webperf/specs/setImmediate/"),
		new Html5SpecScan("page-visibility", "http://www.w3.org/TR/page-visibility/"),
		new Html5SpecScan("animation-timing", "http://www.w3.org/TR/animation-timing/"),
		new Html5SpecScan("webcrypto-api", "https://dvcs.w3.org/hg/webcrypto-api/raw-file/tip/spec/Overview.html"),
		new Html5SpecScan("webaudio", "https://dvcs.w3.org/hg/audio/raw-file/tip/webaudio/specification.html"),
		new Html5SpecScan("web-midi-api", "http://webaudio.github.io/web-midi-api/"),
		new Html5SpecScan("webrtc", "http://dev.w3.org/2011/webrtc/editor/webrtc.html"),
		
		new Html5SpecScan("workers", "http://www.w3.org/TR/workers/"),
		new Html5SpecScan("websockets", "http://www.w3.org/TR/websockets/"),
		new Html5SpecScan("webmessaging", "http://www.w3.org/TR/webmessaging/"),
		new Html5SpecScan("picture-element", "http://www.w3.org/TR/html-picture-element/"),
		*/
		
		//Tizen 2.x Device APIs

	};
	
	public static void init(){
		InputStream iscss = Sources.class.getClass().getResourceAsStream(CSS_SPEC_LINKS);
		InputStream isjs = Sources.class.getClass().getResourceAsStream(JS_SPEC_LINKS);
		List<DocumentationProvider> list = new ArrayList<DocumentationProvider>();
		Properties prop = new Properties();
		try {
			prop.load(iscss);
			iscss.close();
			// read css properties from spec
			Set<Entry<Object, Object>> set = prop.entrySet();
			for (Entry<Object, Object> entry : set){
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				DocumentationProvider scan = new CrosswalkCSSSpecScan(key, value);
				list.add(scan);
			}
			prop = new Properties();
			prop.load(isjs);
			isjs.close();
			// read js interfaces from spec
			set = prop.entrySet();
			for (Entry<Object, Object> entry : set){
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				DocumentationProvider scan = new Html5SpecScan(key, value);
				list.add(scan);
			}
			
			SOURCES = list.toArray(SOURCES);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
