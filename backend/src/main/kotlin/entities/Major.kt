package entities

import javax.persistence.*

@Entity
@Table(name = "Major")
class Major {
    @Id
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long = 0

    @Column(name = "majorName", nullable = false)
    var majorName: String = ""

    @Column(name = "coopOnly", nullable = false)
    var coopOnly: Boolean = true

    @Column(name = "isDoubleDegree", nullable = false)
    var isDoubleDegree: Boolean = false
}