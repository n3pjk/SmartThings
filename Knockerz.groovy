/**
 *  Knockerz
 *
 *  Author: paul.knight@delmarvacomputer.com
 *  Date: 6/17/17
 *
 *  Based on the work of brian@bevey.org in 2013.
 *
 *  Notifies when someone knocks on a door, but does not open it.
 *  Alerts are by push, SMS, PushBullet, audio, and/or by
 *  turning on a switch and/or dimming the device.
 *
 *  Added notification restrictions 2/12/20
 */

definition(
    name: "Knockerz",
    namespace: "dca",
    author: "paul.knight@delmarvacomputer.com",
    description: "Alerts when there is a knock at a door.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png"
)

preferences {
  page name:"pageSetup"
  page name:"pageDoors"
  page name:"pageNotifications"
  page name:"pageRestrictions"
  page name:"pageAbout"
}

/**
 * PAGE METHODS
 **/

/**
 * Main page
 * The "Doors" and "Notification Options" configuration pages, along
 * with the "About" page are accessed from this page. The SmartApp
 * can also be renamed or uninstalled from here.
 *
 * @return a dynamically created main page
 */
def pageSetup() {
  LOG("pageSetup()")

  def pageProperties = [
    name:       "pageSetup",
    nextPage:   null,
    install:    true,
    uninstall:  state.installed
  ]

  return dynamicPage(pageProperties) {
    section("Setup Menu") {
      href "pageDoors", title:"Add/Remove Doors", description:"Tap to open"
      href "pageNotifications", title:"Notification Options", description:"Tap to open"
      href "pageRestrictions", title:"Notification Restrictions", description:"Tap to open"
      href "pageAbout", title:"About Knockerz", description:"Tap to open"
    }
    section([title:"Options", mobileOnly:true]) {
      label title:"Assign a name", required:false
    }
  }
}

/**
 * "About" page
 * Identifies the auther and license.
 *
 * @return a dynamically created "About" page
 */
def pageAbout() {
  LOG("pageAbout()")

  def textAbout =
    "Version ${getVersion()}\n${textCopyright()}\n\n" +
    "You can contribute to the development of this app by making a " +
    "donation to paul.knight@delmarvacomputer.com via PayPal."

  // This URL has not been created yet
  def hrefInfo = [
    url:        "http://delmarvacomputer.github.io/knockerz/",
    style:      "embedded",
    title:      "Tap here for more information...",
    description:"http://delmarvacomputer.github.io/knockerz/",
    required:   false
  ]

  def pageProperties = [
    name:       "pageAbout",
    nextPage:   "pageSetup",
    uninstall:  false
  ]

  return dynamicPage(pageProperties) {
    section("About") {
      paragraph textAbout
      //href hrefInfo
    }
    section("License") {
      paragraph textLicense()
    }
  }
}

/**
 * "Doors" page
 * Configure acceleration, contact sensors, and delay to wait after
 * a knock is detected to see if the door opens.
 *
 * @return a dynamically created "Doors" page
 */
def pageDoors() {
  LOG("pageDoors()")

  def helpAbout =
    "Select acceleration and contact sensors, then " +
    "set delay after knock to see if door opens."

  def inputAccelerationSensors = [
    name:           "accelerationSensors",
    title:          "Listen For Knocks At",
    type:           "capability.accelerationSensor",
    multiple:       true,
    required:       true
  ]

  def inputContactSensors = [
    name:           "contactSensors",
    title:          "See If These Doors Open",
    type:           "capability.contactSensor",
    multiple:       true,
    required:       true
  ]

  def inputKnockDelay = [
    name:           "knockDelay",
    title:          "Knock Delay (default 5s)",
    type:           "number",
    required:       false
  ]

  def pageProperties = [
    name:           "pageDoors",
    nextPage:       "pageSetup",
    uninstall:      false
  ]

  return dynamicPage(pageProperties) {
    section("Add/Remove Doors") {
      paragraph helpAbout
    }
    section("Select Doors") {
      input inputAccelerationSensors
      input inputContactSensors
      input inputKnockDelay
    }
  }
}

/**
 * "Notification Options" page
 * Define what happens if someone is actually knocking on a door.
 * Switches can be turned on, and, optionally dimmed. A message
 * can be sent via push, text, PushBullet, or read aloud.
 *
 * @return a dynamically created "Notifications Options" page
 */
def pageNotifications() {
  LOG("pageNotifications()")

  def helpAbout =
    "How do you want to be notified of a knock at a " +
    "door? Turn on a switch, a chime, or dim a light. " +
    "Send a push or SMS message. Use PushBullet " +
    "or an audio announcement."

  def inputSwitches = [
    name:           "switches",
    type:           "capability.switch",
    title:          "Set these switches",
    multiple:       true,
    required:       false
  ]

  def inputDimmerLevel = [
    name:           "dimmerLevel",
    type:           "enum",
    metadata:       [values:["10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"]],
    title:          "Dimmer Level",
    defaultValue:   "40%",
    required:       false
  ]

  def inputMessageText = [
    name:           "messageText",
    type:           "text",
    title:          "Message Phrase",
    defaultValue:   "%door detected a knock.",
    required:       fale
  ]

  def inputSendPush = [
    name:           "sendPush",
    type:           "bool",
    title:          "Send Push on Knock",
    defaultValue:   true
  ]

  def inputContacts = [
    name:           "contacts",
    type:           "contact",
    title:          "Send notifications to",
    multiple:       true,
    required:       false
  ]

  def inputPhone1 = [
    name:           "phone1",
    type:           "phone",
    title:          "Send to this number",
    required:       false
  ]

  def inputPhone2 = [
    name:           "phone2",
    type:           "phone",
    title:          "Send to this number",
    required:       false
  ]

  def inputPhone3 = [
    name:           "phone3",
    type:           "phone",
    title:          "Send to this number",
    required:       false
  ]

  def inputPhone4 = [
    name:           "phone4",
    type:           "phone",
    title:          "Send to this number",
    required:       false
  ]

  def inputPushbulletDevice = [
    name:           "pushbullet",
    type:           "device.pushbullet",
    title:          "Which Pushbullet devices?",
    multiple:       true,
    required:       false
  ]

  def inputAudioPlayers = [
    name:           "audioPlayers",
    type:           "capability.musicPlayer",
    title:          "Which audio players?",
    multiple:       true,
    required:       false
  ]

  def inputSpeechText = [
    name:           "speechText",
    type:           "text",
    title:          "Knock Phrase",
    defaultValue:   "There is a knock at the %door",
    required:       false
  ]

  def inputEchoDevice = [
    name:           "echoSpeaks",
    type:           "Echo Speaks Device",
    title:          "Select an Amazon Echo Device",
    multiple:       false,
    required:       false
  ]

  def pageProperties = [
    name:           "pageNotifications",
    nextPage:       "pageSetup",
    uninstall:      false
  ]

  return dynamicPage(pageProperties) {
    section("Notification Options") {
      paragraph helpAbout
    }
    section("Turn On Switches") {
      input inputSwitches
      input inputDimmerLevel
    }
    section("Push & SMS Notifications") {
      input inputMessageText
      input("contacts", "contact", title: "Send notification to") {
        input inputSendPush
        input inputPhone1
        input inputPhone2
        input inputPhone3
        input inputPhone4
      }
    }
    section("Pushbullet Notifications") {
      input inputPushbulletDevice
    }
    section("Audio Notifications") {
      input inputEchoDevice
      input inputAudioPlayers
      input inputSpeechText
    }
  }
}

/**
 * "Notification Restrictions" page
 * Define when to allow notifications to be sent. Based on time
 * of day, day of week, modes or switches in a defined state.
 *
 * @return a dynamically created "Notification Restrictions" page
 */
def pageRestrictions() {
  LOG("pageRestrictions()")

  def helpAbout =
    "Restrict when you will receive door knock notifications " +
    "by time of day, day of week, house mode, or when one or " +
    "more switches are on or off."

  def inputStartTime = [
    name:           "startTime",
    type:           "time",
    title:          "Starting time",
    required:       false
  ]

  def inputStopTime = [
    name:           "stopTime",
    type:           "time",
    title:          "Ending time",
    required:       false
  ]

  def inputWeekDays = [
    name:           "weekDays",
    type:           "enum",
    options:        ["Sunday": "Sunday","Monday": "Monday","Tuesday": "Tuesday","Wednesday": "Wednesday","Thursday": "Thursday","Friday": "Friday","Saturday": "Saturday"],
    title:          "These days of the week",
    multiple:       true,
    required:       false
  ]

  def inputModes = [
    name:           "modes",
    type:           "mode",
    title:          "These modes",
    multiple:       true,
    required:       false
  ]

  def inputSwitches = [
    name:           "notifySwitches",
    type:           "capability.switch",
    title:          "These switches",
    multiple:       true,
    required:       false
  ]

  def inputSwitchState = [
    name:           "notifySwitchState",
    type:           "enum",
    metadata:       [values:["On","Off"]],
    title:          "Are",
    defaultValue:   "On",
    multiple:       false,
    required:       true
  ]

  def pageProperties = [
    name:           "pageRestrictions",
    nextPage:       "pageSetup",
    uninstall:      false
  ]

  return dynamicPage(pageProperties) {
    section("Notification Restrictions") {
      paragraph helpAbout
    }
    section("Notify between") {
      input inputStartTime
      input inputStopTime
    }
    section("Notify on") {
      input inputWeekDays
    }
    section("Notify when the house is in") {
      input inputModes
    }
    section("Notify when") {
      input inputSwitches
      input inputSwitchState
    }
  }
}

def installed() {
    LOG("installed()")

    initialize()
    state.installed = true
}

def updated() {
    LOG("updated()")

    unsubscribe()
    initialize()
}

def initialize() {
  log.info "Knockerz. Version ${getVersion()}. ${textCopyright()}"
  LOG("settings: ${settings}")

  state.lastClosed = 0
  subscribe(settings.accelerationSensors, "acceleration.active", onMovement)
  subscribe(settings.contactSensors, "contact.closed", onContact)

  STATE()
}

/**
 * EVENT HANDLERS
 **/

/**
 * Check the specific contact sensor to see if the door is open or
 * was openned in the last 60 seconds.
 *
 * @param a map containing the name of the detecting acceleration sensor.
 */
def checkMultiSensor(data) {
  LOG("checkMultiSensor(${data.name})")

  def contactSensor = settings.contactSensors.find{ it.label == "${data.name}" || it.name == "${data.name}" }
  LOG("Using ${contactSensor?.label ?: contactSensor?.name} contact sensor")
  if ((contactSensor?.latestValue("contact") == "closed") && (now() - (60 * 1000) > state.lastClosed)) {
    LOG("${data.name} detected a knock.")
    notify("${data.name}")
  } else {
    LOG("${data.name} detected acceleration, but appears to be just someone opening the door.")
  }
}

/**
 * Check if any door is open or was openned in the last 60 seconds.
 *
 * @param a map containing the name of the detecting acceleration sensor.
 */
def checkAnySensor(data) {
  LOG("checkAnySensor(${data.name})")

  if (settings.contactSensors.any { it.latestValue("contact") == "open" }) {
    LOG("${data.name} knocked, but a door is open.")
  } else {
    if (now() - (60 * 1000) > state.lastClosed) {
      LOG("${data.name} detected a knock.")
      notify("${data.name}")
    } else {
      LOG("${data.name} detected acceleration, but appears to be just someone opening the door.")
    }
  }
}

/**
 * Acceleration Event Handler
 * Use one of the check handlers depending on whether we can
 * specifically identify the contact sensor or not.
 *
 * @param an acceleration event object
 */
def onMovement(evt) {
  LOG("onMovement(${evt.displayName})")

  def delay = (settings.knockDelay == null) ? 5 : settings.knockDelay
  def contactSensor = settings.contactSensors.find{ it.label == "${evt.displayName}" || it.name == "${evt.displayName}" }
  if (contactSensor) {
    runIn(delay, "checkMultiSensor", [data: [name: "${evt.displayName}"]])
  } else {
    LOG("${evt.displayName} is a ${accelerationSensor.name}")
    runIn(delay, "checkAnySensor", [data: [name: "${evt.displayName}"]])
  }
}

/**
 * Contact Event Handler
 * Saves the last time a contact was closed.
 *
 * @param a contact event object
 */
def onContact(evt) {
  LOG("onContact(${evt.displayName})")
  state.lastClosed = now()
}

/**
 * NOTIFICATION HANDLERS
 **/

/**
 * Main notification processor
 * Turns on and dims switches, calls additional notification methods.
 *
 * @param the name of the acceleration sensor that detected the knock.
 */
private notify(name) {
  LOG("notify(${name})")

  // Determine if conditions permit notification
  def restricted = notifyRestrictions()
  if (!restricted) {
    def msg = textMessage(name)

    // Only turn on those switches that are currently off
    def switchesOn = settings.switches?.findAll { it?.currentSwitch == "off" }
    LOG("switchesOn: ${switchesOn}")
    if (switchesOn) {
      switchesOn*.on()
    }

    // TODO: Add camera support?
    //settings.cameras*.take()

    if (contacts) {
      notifyContacts(msg)
    } else {
      notifyPush(msg)
      notifyText(msg)
    }
    notifyPushBullet(msg)
    notifyEcho(name)
    notifyVoice(name)
  } else {
    LOG("notification restricted")
  }
}

/**
 * Check restrictions to notification
 */
private def notifyRestrictions() {
  LOG("notifyRestrictions()")

  // Create and ensure the data object is set to local time zone
  def df = new java.text.SimpleDateFormat("EEEE")
  df.setTimeZone(location.timeZone)

  // Is today a selected day of the week?
  if (settings.weekDays) {
    def day = df.format(new Date())
    def dayCheck = settings.weekDays.contains(day)
    if (!dayCheck) {
      LOG("Not an allowed weekday")
      return true
    }
  }

  // Is the time within the specified interval?
  if (settings.startTime && settings.stopTime) {
    def timeCheck = timeOfDayIsBetween(settings.startTime, settings.stopTime, new Date(), location.timeZone)
    if (!timeCheck) {
      LOG("Outside time of day")
      return true
    }
  }

  // Is the house in a selected mode?
  if (settings.modes) {
    def modeCheck = settings.modes.contains(location.currentMode)
    if (!modeCheck) {
      LOG("Not allowed in ${location.currentMode} mode")
      return true
    }
  }

  // Are any switches set to disable notifications?
  def switchCheck = settings.notifySwitches?.findAll { it?.currentSwitch != settings.notifySwitchState }
  if (switchCheck) {
    LOG("Switches not ${settings.notifySwitchState}: ${switchCheck}")
    return true
  }

  return false
}

/**
 * Process message to Contact Book
 *
 * @param the message to send
 */
private def notifyContacts(msg) {
  LOG("notifyContacts(${msg})")

  sendNotificationToContacts(msg, contacts)
}

/**
 * Process a push message
 *
 * @param the message to send
 */
private def notifyPush(msg) {
  LOG("notifyPush(${msg})")

  if (settings.sendPush) {
    // sendPush can throw an exception
    try {
      sendPush(msg)
    } catch (e) {
      log.error e
    }
  } else {
    sendNotificationEvent(msg)
  }
}

/**
 * Process a text message
 *
 * @param the message to send
 */
private def notifyText(msg) {
  LOG("notifyText(${msg})")

  if (settings.phone1) {
    sendSms(phone1, msg)
  }

  if (settings.phone2) {
    sendSms(phone2, msg)
  }

  if (settings.phone3) {
    sendSms(phone3, msg)
  }

  if (settings.phone4) {
    sendSms(phone4, msg)
  }
}

/**
 * Process a PushBullet message
 *
 * @param the message to send
 */
private def notifyPushBullet(msg) {
  if (settings.pushbullet) {
    settings.pushbullet*.push(location.name, msg)
  }
}

/**
 * Process a text-to-speech message. Note that the string
 * '%door' in the message text will be replaced with the
 * name of the acceleration sensor that detected the knock.
 *
 * @param the name of the acceleration sensor that detected the knock.
 */
private def notifyVoice(name) {
  LOG("notifyVoice(${name})")

  if (!settings.audioPlayers) {
    return
  }

  // Replace %door with name
  def phrase = textSpeech(name)

  if (phrase) {
    settings.audioPlayers*.playText(phrase)
  }
}

private def notifyEcho(name) {
  LOG("notifyEcho(${name})")

  if (!settings.echoSpeaks) {
    return
  }

  // Replace %door with name
  def phrase = textSpeech(name)

  if (phrase) {
    settings.echoSpeaks*.playAnnouncementAll(phrase,"Knockerz")
  }
}

private def textMessage(name) {
  def text = settings.messageText.replaceAll('%door', name)
}

private def textSpeech(name) {
  def text = settings.speechText.replaceAll('%door', name)
}

private def getVersion() {
  return "1.0.0"
}

private def textCopyright() {
  def text = "Copyright © 2017 Delmarva Computer Associates LLC"
}

private def textLicense() {
  def text =
    "This program is free software: you can redistribute it and/or " +
    "modify it under the terms of the GNU General Public License as " +
    "published by the Free Software Foundation, either version 3 of " +
    "the License, or (at your option) any later version.\n\n" +
    "This program is distributed in the hope that it will be useful, " +
    "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU " +
    "General Public License for more details.\n\n" +
    "You should have received a copy of the GNU General Public License " +
    "along with this program. If not, see <http://www.gnu.org/licenses/>."
}

private def LOG(message) {
  log.trace message
}

private def STATE() {
  log.trace "state: ${state}"
}