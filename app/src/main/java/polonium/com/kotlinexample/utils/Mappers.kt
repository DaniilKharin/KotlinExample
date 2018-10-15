package polonium.com.kotlinexample.utils

import com.google.android.gms.vision.barcode.Barcode
import com.vicpin.krealmextensions.saveManaged
import io.realm.Realm
import io.realm.RealmList
import polonium.com.kotlinexample.data.*
import java.util.*

fun Barcode.toBarcodeRealm(realm : Realm): BarcodeRealm {
    return BarcodeRealm(
            UUID.randomUUID().toString(),
            this.format,
            this.rawValue,
            this.displayValue,
            this.valueFormat,
            this.email?.toEmailRealm(),
            this.phone?.toPhoneRealm(),
            this.sms?.toSmsRealm(),
            this.wifi?.toWifiRealm(),
            this.url?.toUrlRealm(),
            this.geoPoint?.toGeoPointRealm(),
            this.calendarEvent?.toCalendarEventRealm(),
            this.contactInfo?.toContactInfoRealm(),
            this.driverLicense?.toDriverLicenseRealm()).saveManaged(realm)
}

fun Barcode.Email.toEmailRealm(): EmailRealm = EmailRealm(
        this.type,
        this.address,
        this.body,
        this.subject)

fun Barcode.Phone.toPhoneRealm(): PhoneRealm = PhoneRealm(
        this.type,
        this.number)

fun Barcode.Address.toAddressRealm(): AddressRealm {
    val addressLinesRealm = RealmList<String>()
    addressLinesRealm.addAll(this.addressLines)
    return AddressRealm(
            this.type,
            addressLinesRealm)
}

fun Barcode.PersonName.toPersonNameRealm(): PersonNameRealm = PersonNameRealm(
        this.formattedName,
        this.pronunciation,
        this.prefix,
        this.first,
        this.middle,
        this.last,
        this.suffix
)


fun Barcode.Sms.toSmsRealm(): SmsRealm = SmsRealm(this.message, this.phoneNumber)

fun Barcode.WiFi.toWifiRealm(): WifiRealm = WifiRealm(
        this.ssid,
        this.password,
        this.encryptionType
)

fun Barcode.UrlBookmark.toUrlRealm(): UrlRealm = UrlRealm(this.title, this.url)

fun Barcode.GeoPoint.toGeoPointRealm(): GeoPointRealm = GeoPointRealm(this.lat, this.lng)

fun Barcode.CalendarDateTime.toCalendarDateTimeRealm(): CalendarDateTimeRealm = CalendarDateTimeRealm(
        this.year,
        this.month,
        this.day,
        this.hours,
        this.minutes,
        this.seconds,
        this.isUtc,
        this.rawValue
)

fun Barcode.CalendarEvent.toCalendarEventRealm(): CalendarEventRealm = CalendarEventRealm(
        this.summary,
        this.description,
        this.location,
        this.organizer,
        this.status,
        this.start?.toCalendarDateTimeRealm(),
        this.end?.toCalendarDateTimeRealm()
)

fun toRealmPhonesList(source: Array<Barcode.Phone>): RealmList<PhoneRealm> {
    val list = RealmList<PhoneRealm>()
    for (phone in source)
        list.add(phone.toPhoneRealm())
    return list
}

fun toRealmEmailList(source: Array<Barcode.Email>): RealmList<EmailRealm> {
    val list = RealmList<EmailRealm>()
    for (email in source)
        list.add(email.toEmailRealm())
    return list
}

fun toRealmAddressList(source: Array<Barcode.Address>): RealmList<AddressRealm> {
    val list = RealmList<AddressRealm>()
    for (address in source)
        list.add(address.toAddressRealm())
    return list
}


fun Barcode.ContactInfo.toContactInfoRealm(): ContactInfoRealm {
    val urlRealmList = RealmList<String>()
    urlRealmList.addAll(urls)
    return ContactInfoRealm(
            this.name?.toPersonNameRealm(),
            this.organization,
            this.title,
            toRealmPhonesList(this.phones),
            toRealmEmailList(this.emails),
            urlRealmList,
            toRealmAddressList(this.addresses))
}

fun Barcode.DriverLicense.toDriverLicenseRealm(): DriverLicenseRealm = DriverLicenseRealm(
        this.documentType,
        this.firstName,
        this.lastName,
        this.middleName,
        this.gender,
        this.addressStreet,
        this.addressCity,
        this.addressState,
        this.addressZip,
        this.licenseNumber,
        this.issueDate,
        this.expiryDate,
        this.birthDate,
        this.issuingCountry
)



/*fun BarcodeRealm.toBarcode(): Barcode = Barcode(
        this.format,
        this.rawValue,
        this.displayValue,
        this.valueFormat,
        null,
        this.email.toEmail(),
        this.phone.toPhone(),
        this.sms,
        this.wifi,
        this.url,
        this.geoPoint,
        this.calendarEvent,
        this.contactInfo,
        this.driverLicense)

fun EmailRealm.toEmail(): Barcode.Email = Barcode.Email(
        this.type,
        this.address,
        this.body,
        this.subject)

fun PhoneRealm.toPhone(): Barcode.Phone = Barcode.Phone(
        this.type,
        this.number)*/