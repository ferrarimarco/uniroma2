
<%@ include file="/common/taglibs.jsp"%>
<head>
    <title><fmt:message key="goalDetail.title"/></title>
    <meta name="menu" content="DefinitionPhaseMenu"/>
</head>
 
<div class="span2">
    <h2><fmt:message key='goal.heading'/></h2>
    <p><fmt:message key="goal.message"/></p>
    <c:choose>
    	<c:when test="${goal.goalOwner eq currentUser}">You are <b>Goal Owner</b> for this Goal.</c:when>
		<c:when test="${goal.goalEnactor eq currentUser}">You are <b>GoalEnactor</b> for this Goal.</c:when>		
		<c:when test="${goal.QSMembers.contains(currentUser)}">You are <b>Question Stakeholder</b> for this Goal.</c:when>
		<c:when test="${goal.MMDMMembers.contains(currentUser)}">You are <b>Metric Model Data Manager</b> for this Goal.</c:when>
		<c:when test="${goal.project.projectManagers.contains(currentUser)}">You are <b>Project Manager</b> for this Project.</c:when>		
	</c:choose>
</div>


<div class="span7">
    <form:errors path="*" cssClass="alert alert-error fade in" element="div"/>
    <form:form commandName="goal" method="post" action="goalform" id="goalForm"
               cssClass="well form-horizontal">
    <form:hidden path="id"/>

    <spring:bind path="goal.project">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.project"/>
        <div class="controls">
        	<form:select path="project.id" onchange="" >
					<form:option value="${goal.project.id}" label="${goal.project.name}"/>
			</form:select>  
            <form:errors path="project" cssClass="help-inline"/>
        </div>
    </div>

    <spring:bind path="goal.description">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.description"/>
        <div class="controls">
            <form:input path="description" id="description" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="description" cssClass="help-inline"/>
        </div>
    </div>

    <spring:bind path="goal.type">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.type"/>
        <div class="controls">
            <form:input path="type" id="type" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="type" cssClass="help-inline"/>
        </div>
    </div>     
            
    <spring:bind path="goal.interpretationModel">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.interpretationModel"/>
        <div class="controls">
        	<form:select path="interpretationModel" 
        		onchange="if(this.form.interpretationModel.value != 2) {document.getElementById('divGQMStrategy').style.display='none';document.getElementById('divGQM').style.display='block';} else {document.getElementById('divGQMStrategy').style.display='block';document.getElementById('divGQM').style.display='none';}"  
        			disabled="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}">
        		<form:option value="" label="-- Select item --"/>
				<form:option value="1" label="GQM"/>
				<form:option value="2" label="GQM+Strategies"/>								
			</form:select>
            <form:errors path="interpretationModel" cssClass="help-inline"/>
        </div>
    </div>

    <spring:bind path="goal.scope">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.scope"/>
        <div class="controls">
            <form:input path="scope" id="scope" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="scope" cssClass="help-inline"/>
        </div>
    </div>

    <spring:bind path="goal.focus">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.focus"/>
        <div class="controls">
            <form:input path="focus" id="focus" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="focus" cssClass="help-inline"/>
        </div>
    </div>
	<c:choose>
    	<c:when test="${goal.interpretationModel ne 2}">
    		<div id="divGQM" >
		</c:when>
		<c:otherwise>
			<div id="divGQM" hidden="true">
		</c:otherwise>
    </c:choose>	    
    
    <spring:bind path="goal.subject">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.subject"/>
        <div class="controls">
            <form:input path="subject" id="subject" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="subject" cssClass="help-inline"/>
        </div>
    </div>


    <spring:bind path="goal.context">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.context"/>
        <div class="controls">
            <form:input path="context" id="context" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="context" cssClass="help-inline"/>
        </div>
    </div>

    <spring:bind path="goal.viewpoint">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.viewpoint"/>
        <div class="controls">
            <form:input path="viewpoint" id="viewpoint" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="viewpoint" cssClass="help-inline"/>
        </div>
    </div>

    <spring:bind path="goal.impactOfVariation">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.impactOfVariation"/>
        <div class="controls">
            <form:input path="impactOfVariation" id="impactOfVariation" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
            <form:errors path="impactOfVariation" cssClass="help-inline"/>
        </div>
    </div>
		     
</div>

	<c:choose>
    	<c:when test="${goal.interpretationModel eq 2}">
    		<div id="divGQMStrategy" >
		</c:when>
		<c:otherwise>
			<div id="divGQMStrategy" hidden="true">
		</c:otherwise>
    </c:choose>	
    
	        <spring:bind path="goal.activity">
		    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
		    </spring:bind>
		        <appfuse:label styleClass="control-label" key="goal.activity"/>
		        <div class="controls">
		            <form:input path="activity" id="activity" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
		            <form:errors path="activity" cssClass="help-inline"/>
		        </div>
		    </div>
 
 	        <spring:bind path="goal.object">
		    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
		    </spring:bind>
		        <appfuse:label styleClass="control-label" key="goal.object"/>
		        <div class="controls">
		            <form:input path="object" id="object" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
		            <form:errors path="object" cssClass="help-inline"/>
		        </div>
		    </div>   


 	        <spring:bind path="goal.magnitude">
		    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
		    </spring:bind>
		        <appfuse:label styleClass="control-label" key="goal.magnitude"/>
		        <div class="controls">
		            <form:input path="magnitude" id="magnitude" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
		            <form:errors path="magnitude" cssClass="help-inline"/>
		        </div>
		    </div>  

 	        <spring:bind path="goal.timeframe">
		    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
		    </spring:bind>
		        <appfuse:label styleClass="control-label" key="goal.timeframe"/>
		        <div class="controls">
		            <form:input path="timeframe" id="timeframe" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
		            <form:errors path="timeframe" cssClass="help-inline"/>
		        </div>
		    </div> 		
		    
 	        <spring:bind path="goal.constraints">
		    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
		    </spring:bind>
		        <appfuse:label styleClass="control-label" key="goal.constraints"/>
		        <div class="controls">
		            <form:input path="constraints" id="constraints" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
		            <form:errors path="constraints" cssClass="help-inline"/>
		        </div>
		    </div> 	

 	        <spring:bind path="goal.relations">
		    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
		    </spring:bind>
		        <appfuse:label styleClass="control-label" key="goal.relations"/>
		        <div class="controls">
		            <form:input path="relations" id="relations" maxlength="255" readonly="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"/>
		            <form:errors path="relations" cssClass="help-inline"/>
		        </div>
		    </div> 	
		    		            
			<div class="control-group" id="divStrategy" >
	        <appfuse:label styleClass="control-label" key="goal.strategy"/>
	        <div class="controls">      
	            <form:select id="strategy"  path="strategy.id" onchange="" disabled="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"
	            				cssStyle="width:400px" >
	            	<form:option value="">None</form:option>
	            	<form:options items="${strategies}" itemValue="id" itemLabel="name"   />
				</form:select>            	
	            <form:errors path="strategy" cssClass="help-inline"/>
	        </div>
	    </div> 

		</div>

	<div class="control-group">
        <appfuse:label styleClass="control-label" key="goal.parent"/>
        <div class="controls">      
            <form:select path="parent.id" onchange="" disabled="${!((goal.status eq 'DRAFT' || goal.status eq 'FOR_REVIEW') && goal.goalOwner eq currentUser)}"
            				cssStyle="width:400px">
            	<form:option value="">None</form:option>
            	<form:options items="${availableGoals}" itemValue="id" itemLabel="description"   />
			</form:select>            	
            <form:errors path="parent" cssClass="help-inline"/>
        </div>
    </div>  
	                         
    <div class="control-group">
        <appfuse:label styleClass="control-label" key="goal.go"/>
        <div class="controls">      
            <form:select path="goalOwner.id" onchange="">
					<form:option value="${goal.goalOwner.id}" label="${goal.goalOwner.fullName}"/>
			</form:select>            	
            <form:errors path="goalOwner" cssClass="help-inline"/>
        </div>
    </div>    
	

    <div class="control-group">
        <appfuse:label styleClass="control-label" key="goal.ge"/>
        <div class="controls">        	
			<form:select path="goalEnactor.id" onchange=""  disabled="${ ((goal.status ne 'DRAFT') ||  goal.goalOwner ne currentUser) ? 'true':'false'}">
				<form:options items="${availableUsers}" itemValue="id" itemLabel="fullName"   />
			</form:select>            
            <form:errors path="goalEnactor" cssClass="help-inline"/>
        </div>
    </div>   
    
	<c:if test="${visibleGESection}">    
	
	    <div class="control-group">
	        <appfuse:label styleClass="control-label" key="goal.qs"/>
	        <div class="controls">        	
			    <form:select path="QSMembers" multiple="true"  size="6" disabled="${(goal.goalEnactor ne currentUser) || (goal.status eq 'APPROVED')}">
			    	<form:options items="${availableUsers}" itemLabel="fullName" itemValue="id"/>	
			    </form:select>       
	            <form:errors path="QSMembers" cssClass="help-inline"/>
	        </div>
	    </div>   
	
		<spring:bind path="goal.MMDMMembers">
	    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
	    </spring:bind>
	        <appfuse:label styleClass="control-label" key="goal.mmdm"/>
	        <div class="controls">
	        	<form:select path="MMDMMembers" onchange="" disabled="${(goal.goalEnactor ne currentUser) || (goal.status eq 'APPROVED')}">        		
	        		<form:options items="${availableUsers}" itemValue="id" itemLabel="fullName"   />								
				</form:select>
	            <form:errors path="MMDMMembers" cssClass="help-inline"/>
	        </div>
	    </div>
	            
	</c:if>
	
	
    <spring:bind path="goal.status">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
    </spring:bind>
        <appfuse:label styleClass="control-label" key="goal.status"/>
        <div class="controls">
			<form:select path="status" multiple="false" >
		    	<form:options items="${availableStatus}"/>
		    </form:select>		
		    <c:if test="${goal.status eq 'PROPOSED' && goal.project.projectManagers.contains(currentUser)}">
		    
				<c:set var="justVoted" value="false" />
				<c:forEach var="item" items="${goal.votes}">
  					<c:if test="${item eq currentUser}">
    					<c:set var="justVoted" value="true" />
  					</c:if>
				</c:forEach>	
				<c:choose>
					<c:when test="${justVoted}">					
						<input type="checkbox" name="vote" value="true" checked="checked" disabled="disabled">  Accepted by ${goal.numberOfVote}/${goal.quorum} Project Managers</input>
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="vote" value="true">  Mark as accepted. Is just accepted by ${goal.numberOfVote}/${goal.quorum} Project Managers</input>
					</c:otherwise>
				</c:choose>	    		    
		    </c:if>
		    		    
            <form:errors path="status" cssClass="help-inline"/>
        </div>
    </div>

    <c:if test="${(((goal.goalEnactor eq currentUser) || (goal.goalOwner eq currentUser)) && (goal.status eq 'FOR_REVIEW' || goal.status eq 'ACCEPTED'))}">
	    <spring:bind path="goal.refinement">
	    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
	    </spring:bind>
	        <appfuse:label styleClass="control-label" key="goal.refinement"/>
	        <div class="controls">
	            <form:input path="refinement" id="refinement" maxlength="255" readonly="${goal.goalOwner eq currentUser}"	/>
	            <form:errors path="refinement" cssClass="help-inline"/>
	        </div>
	    </div>
    </c:if>
    
    <div class="form-actions">
        <button type="submit" class="btn btn-primary" name="save">
            <i class="icon-ok icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty goal.id && goal.status eq 'DRAFT' && goal.goalOwner eq currentUser}">
          <button type="submit" class="btn" name="delete">
              <i class="icon-trash"></i> <fmt:message key="button.delete"/>
          </button>
        </c:if>
        <button type="submit" class="btn" name="cancel">
            <i class="icon-remove"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
    </form:form>
</div>


<c:if test="${(goal.interpretationModel eq 2) && (goal.status eq 'ACCEPTED') && (goal.goalEnactor eq currentUser)}">
	<div class="span2">
	    <h2><fmt:message key="goal.splitting"/></h2>
	    <form:form commandName="goal" method="post" action="goalsplit" id="goalsplit"
	               cssClass="well form-vertival">
	    	<form:hidden path="id"/>    	
	    	
	    	<img alt="GQM+Strategies" src="images/GQM+Strategies_logo.jpg"/>
	    	
	    	<div>
	    		<br/>	    	
	    		Number of split:
	    		<select id="split" name="split" style="width:80px;">			  
				  <option value="2">2</option>
				  <option value="3">3</option>
				  <option value="4">4</option>
				  <option value="5">5</option>
				  <option value="6">6</option>
				  <option value="7">7</option>
				  <option value="8">8</option>
				  <option value="9">9</option>
				  <option value="10">10</option>			  
				</select> 
	    	</div>
		    <br/>
	        <button type="submit" class="btn btn-primary" name="split">
	            <i class="icon-ok icon-white"></i> <fmt:message key="button.split"/>
	        </button>	    	
		</form:form>    
	</div>
</c:if>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['goalForm']).focus();
    });
    
    jQuery(function($) {
        $('form').bind('submit', function() {
            $(this).find(':input').removeAttr('disabled');
        });

    });
</script>
</script>