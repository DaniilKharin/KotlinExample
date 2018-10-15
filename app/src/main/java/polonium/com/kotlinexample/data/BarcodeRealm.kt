package polonium.com.kotlinexample.data

import com.google.android.gms.vision.barcode.Barcode
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmModule
import java.util.*

@RealmClass
open class BarcodeRealm(
        @PrimaryKey
        var ID: String? = UUID.randomUUID().toString(),
        var format: Int? = 0,
        var rawValue: String? = null,
        var displayValue: String? = null,
        var valueFormat: Int? = 0,
        var email: EmailRealm? = EmailRealm(),
        var phone: PhoneRealm? = PhoneRealm(),
        var sms: SmsRealm? = SmsRealm(),
        var wifi: WifiRealm? = WifiRealm(),
        var url: UrlRealm? = UrlRealm(),
        var geoPoint: GeoPointRealm? = GeoPointRealm(),
        var calendarEvent: CalendarEventRealm? = CalendarEventRealm(),
        var contactInfo: ContactInfoRealm? = ContactInfoRealm(),
        var driverLicense: DriverLicenseRealm? = DriverLicenseRealm()
) : RealmModel {
}


open class EmailRealm(var type: Int = 0,
                      var address: String? = null,
                      var subject: String? = null,
                      var body: String? = null
) : RealmObject()

open class PhoneRealm(
        var type: Int = 0,
        var number: String? = null
) : RealmObject()

open class SmsRealm(
        var message: String? = null,
        var phoneNumber: String? = null
) : RealmObject()

open class WifiRealm(
        var ssid: String? = null,
        var password: String? = null,
        var encryptionType: Int = 0
) : RealmObject()

open class UrlRealm(
        var title: String? = null,
        var url: String? = null
) : RealmObject()

open class GeoPointRealm(
        var lat: Double = 0.0,
        var lng: Double = 0.0
) : RealmObject()

open class CalendarEventRealm(
        var summary: String? = null,
        var description: String? = null,
        var location: String? = null,
        var organizer: String? = null,
        var status: String? = null,
        var start: CalendarDateTimeRealm? = CalendarDateTimeRealm(),
        var end: CalendarDateTimeRealm? = CalendarDateTimeRealm()

) : RealmObject()

open class CalendarDateTimeRealm(
        var year: Int = 0,
        var month: Int = 0,
        var day: Int = 0,
        var hours: Int = 0,
        var minutes: Int = 0,
        var seconds: Int = 0,
        var isUtc: Boolean = false,
        var rawValue: String? = null
) : RealmObject()

open class ContactInfoRealm(
        var name: PersonNameRealm? = PersonNameRealm(),
        var organization: String? = null,
        var title: String? = null,
        var phones: RealmList<PhoneRealm>? = RealmList(),
        var emails: RealmList<EmailRealm>? = RealmList(),
        var urls: RealmList<String>? = RealmList(),
        var addresses: RealmList<AddressRealm>? = RealmList()
) : RealmObject()

open class PersonNameRealm(
        var formattedName: String? = null,
        var pronunciation: String? = null,
        var prefix: String? = null,
        var first: String? = null,
        var middle: String? = null,
        var last: String? = null,
        var suffix: String? =null
) : RealmObject()

open class AddressRealm(
        var type: Int = 0,
        var addressLines: RealmList<String>? = RealmList()
) : RealmObject()


open class DriverLicenseRealm(
        var documentType: String? = null,
        var firstName: String? = null,
        var middleName: String? = null,
        var lastName: String? = null,
        var gender: String? = null,
        var addressStreet: String? = null,
        var addressCity: String? = null,
        var addressState: String? = null,
        var addressZip: String? = null,
        var licenseNumber: String? = null,
        var issueDate: String? = null,
        var expiryDate: String? = null,
        var birthDate: String? = null,
        var issuingCountry: String? =null
) : RealmObject()

@RealmModule(classes = [(BarcodeRealm::class),
    (EmailRealm::class),
    (PhoneRealm::class),
    (SmsRealm::class),
    (WifiRealm::class),
    (UrlRealm::class),
    (GeoPointRealm::class),
    (CalendarEventRealm::class),
    (CalendarDateTimeRealm::class),
    (ContactInfoRealm::class),
    (PersonNameRealm::class),
    (DriverLicenseRealm::class)])
class BarcodeModule