package it.uniroma2.gqm.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.appfuse.model.BaseObject;
import org.appfuse.model.User;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@NamedQueries({
    @NamedQuery(
            name = "findGoalByProject",
            query = "select g from Goal g  where g.project.id= :project_id "
    ),
    @NamedQuery(
            name = "findMeasuredGoal",
            query = "select distinct g from Goal g inner join g.questions gq " +
            		" inner join gq.pk.question q  " +
            		" inner join q.metrics qm " +
            		" inner join qm.pk.metric m " +
            		" where g.project.id= :project_id "
    )
})
public class Goal extends BaseObject {
	private Long id;
	private Project project;	
	private String description;
	private String subject;
	private String scope;
	private String focus;
	private String context;
	private String viewpoint;
	private String type;
	private String impactOfVariation;
	private Integer interpretationModel;
	private User goalOwner;
	private Set<User> QSMembers = new HashSet<User>();
	private Set<User> MMDMMembers = new HashSet<User>();
	private User goalEnactor;
	private GoalStatus status;
	private String refinement;
	private Set<User> votes = new HashSet<User>();
	private Set<Goal> children = new HashSet<Goal>();
	private Goal parent;
	private Set<GoalQuestion> questions = new HashSet<GoalQuestion>();
	private Strategy strategy;	
	private String activity;
	private String object;
	private String magnitude;
	private String timeframe;
	private String constraints;
	private String relations;
	
	
	public Goal() {
		;
	}

	public Goal(Long id) {
		this.id = id;
	}

	
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id @Column(name = "goal_id",nullable=false,unique=true)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "description", length = 255, nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	@Column(name = "type", length = 255, nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "subject", length = 255)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "scope", length = 255)
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Column(name = "focus", length = 255)
	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	@Column(name = "context", length = 255)
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Column(name = "viewpoint", length = 255)
	public String getViewpoint() {
		return viewpoint;
	}

	public void setViewpoint(String viewpoint) {
		this.viewpoint = viewpoint;
	}

	@Column(name = "impact_of_variation", length = 255)
	public String getImpactOfVariation() {
		return impactOfVariation;
	}

	public void setImpactOfVariation(String impactOfVariation) {
		this.impactOfVariation = impactOfVariation;
	}

	@Column(name = "interpretation_model", length = 255)
	public Integer getInterpretationModel() {
		return interpretationModel;
	}

	public void setInterpretationModel(Integer interpretationModel) {
		this.interpretationModel = interpretationModel;
	}

	@Transient
	public String getInterpretationModelAsString() {
		if(this.interpretationModel == null)
			return "Not specified";
		if (this.interpretationModel == 1) {
			return "GQM";
		} else if (this.interpretationModel == 2) {
			return "GQM+Strategies";
		} else {
			return "Not specified";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Goal other = (Goal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Goal [id=" + id + ", description=" + description + "]";
	}


	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 50)
	public GoalStatus getStatus() {
		return status;
	}
	
	public void setStatus(GoalStatus status) {
		this.status = status;
	}
	
	@ManyToOne
	@JoinColumn(name = "strategy_id", nullable = true)	
	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	@ManyToOne
	@JoinColumn(name = "go_id", nullable = false)	
	public User getGoalOwner() {
		return goalOwner;
	}
	public void setGoalOwner(User goalOwner) {
		this.goalOwner = goalOwner;
	}

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(
            name = "goal_qs", 
            joinColumns = { @JoinColumn(name = "goal_id") },
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
	public Set<User> getQSMembers() {
		return QSMembers;
	}
	public void setQSMembers(Set<User> qSMembers) {
		QSMembers = qSMembers;
	}

	@ManyToOne
	@JoinColumn(name = "ge_id", nullable = true)	
	public User getGoalEnactor() {
		return goalEnactor;
	}
	public void setGoalEnactor(User goalEnactor) {
		this.goalEnactor = goalEnactor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	
    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(
            name = "goal_mmdm",
            joinColumns = { @JoinColumn(name = "goal_id") },
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )	
	public Set<User> getMMDMMembers() {
		return MMDMMembers;
	}
	public void setMMDMMembers(Set<User> mMDMMembers) {
		this.MMDMMembers = mMDMMembers;
	}
	
	@Column(name = "refinement", length = 255)
	public String getRefinement() {
		return refinement;
	}

	public void setRefinement(String refinement) {
		this.refinement = refinement;
	}
	
    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name = "goal_vote",
            joinColumns = { @JoinColumn(name = "goal_id") },
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )	
	public Set<User> getVotes() {
		return votes;
	}
    
	public void setVotes(Set<User> votes) {
		this.votes = votes;
	}
	
	@Transient
	public int getNumberOfVote(){
		return getVotes().size();
	}
	
	@Transient
	public int getQuorum(){
		return this.project.getProjectManagers().size();
	}


	@ManyToOne
	@JoinColumn(name = "parent_id", nullable = true)	
	public Goal getParent() {
		return parent;
	}

	public void setParent(Goal parent) {
		this.parent = parent;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.goal")
	public Set<GoalQuestion>  getQuestions() {
		return questions;
	}

	public void setQuestions(Set<GoalQuestion> questions) {
		this.questions = questions;
	}


	@Column(name = "activity", length = 255)
	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	@Column(name = "object", length = 255)
	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	@Column(name = "magnitude", length = 255)
	public String getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(String magnitude) {
		this.magnitude = magnitude;
	}

	@Column(name = "timeframe", length = 255)
	public String getTimeframe() {
		return timeframe;
	}

	public void setTimeframe(String timeframe) {
		this.timeframe = timeframe;
	}
	
	@Column(name = "constraints", length = 255)
	public String getConstraints() {
		return constraints;
	}

	public void setConstraints(String constraints) {
		this.constraints = constraints;
	}
	
	@Column(name = "relations", length = 255)
	public String getRelations() {
		return relations;
	}

	public void setRelations(String relations) {
		this.relations = relations;
	}
		
}
