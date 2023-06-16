package entities

import javax.persistence.*

@Entity
@Table(name = "Joint")
class Joint {
    @Id
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long = 0

    @Column(name = "jointName", nullable = false)
    var jointName: DegreeName = ""
}