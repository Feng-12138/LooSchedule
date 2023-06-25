package entities

import javax.persistence.*

typealias Year = String             // Year which the degree plan is for
typealias RequirementNotes = String // Any additional notes in degree requirements
typealias Url = String              // Link to undergrad calendar for accurate information
@Entity
@Table(name = "Requirement")
class Requirement {
    enum class DegreeType {
        MAJOR,
        MINOR,
        SPECIALIZATION,
        Joint,
        DIPLOMA,
        OPTION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requirementID", nullable = false)
    var requirementID: Long = 0

    @Column(name = "type", nullable = false)
    var type: DegreeType = DegreeType.MAJOR

    @Column(name = "year", nullable = false)
    var year: Year = ""

    @Column(name = "courses")
    var courses: String? = null

    @Column(name = "additionalRequirements")
    var additionalRequirements: RequirementNotes? = null

    @Column(name = "link")
    var link: Url? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    lateinit var major: Major

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    lateinit var minor: Minor

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    lateinit var specialization: Specialization

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    lateinit var joint: Joint
}