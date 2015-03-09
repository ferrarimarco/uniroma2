<html>
<body>
	<!-- Get user info -->
	<div th:if="${#httpServletRequest.remoteUser != null}">
		<p th:text="${#httpServletRequest.remoteUser}">sample_user</p>
	</div>

	<!-- Logout -->
	<form th:action="@{/logout}" method="post">
		<input type="submit" value="Log out" />
	</form>
</body>
</html>
