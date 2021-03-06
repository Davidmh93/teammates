package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;

/**
 * Move a course to the recycle bin.
 */
public class BinCourseAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToBin, userInfo.id),
                logic.getCourse(idOfCourseToBin), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            CourseAttributes courseAttributes = logic.getCourse(idOfCourseToBin);
            courseAttributes.deletedAt = logic.moveCourseToRecycleBin(idOfCourseToBin);

            return new JsonResult(new CourseData(courseAttributes));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
