package entities

import javax.persistence.*

typealias DegreeName = String

@Entity
@Table(name = "Major")
class Major {
    @Id
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long? = null

    @Column(name = "majorName", nullable = false)
    var majorName: DegreeName? = null

    @Column(name = "isCoop", nullable = false)
    var isCoop: Boolean? = null

    @Column(name = "isDoubleDegree", nullable = false)
    var isDoubleDegree: Boolean? = null
}