package guru.qa.niffler.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.UserJsonBulk;
import guru.qa.niffler.service.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@GrpcService
public class GrpcUserService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcUserService.class);

    private final UserService userService;

    @Autowired
    public GrpcUserService(UserService userService) {
        this.userService = userService;

    }

    @Transactional(readOnly = true)
    @Override
    public void currentUser(UsernameRequest request, StreamObserver<UserResponse> responseObserver) {
        UserJson currentUser = userService.getCurrentUser(request.getUsername());
        responseObserver.onNext(toProto(currentUser));
        responseObserver.onCompleted();
    }


    @Override
    public void updateUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserJson dto = UserJson.fromProto(request);
        UserJson saved = userService.update(dto);
        responseObserver.onNext(toProto(saved));
        responseObserver.onCompleted();
    }

    @Override
    public void allUsers(UserSearchRequest request, StreamObserver<UsersResponse> responseObserver) {
        final String search = request.hasSearchQuery()
                ? request.getSearchQuery()
                : null;

        List<UserJsonBulk> users = userService.allUsers(
                request.getUsername(),
                search
        );

        responseObserver.onNext(toProto(users));
        responseObserver.onCompleted();
    }

    @Override
    public void allUsersPage(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
        final String search = request.hasSearchQuery()
                ? request.getSearchQuery()
                : null;
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<UserJsonBulk> users = userService.allUsers(
                request.getUsername(),
                pageable,
                search
        );

        responseObserver.onNext(toProto(users));
        responseObserver.onCompleted();
    }

    @Transactional(readOnly = true)
    @Override
    public void allFriends(UserSearchRequest request, StreamObserver<UsersResponse> responseObserver) {
        final String search = request.hasSearchQuery()
                ? request.getSearchQuery()
                : null;

        List<UserJsonBulk> users = userService.friends(
                request.getUsername(),
                search
        );

        responseObserver.onNext(toProto(users));
        responseObserver.onCompleted();
    }

    @Transactional(readOnly = true)
    @Override
    public void allFriendsPage(UserPageRequest request, StreamObserver<UserPageResponse> responseObserver) {
        final String search = request.hasSearchQuery()
                ? request.getSearchQuery()
                : null;
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize()
        );

        Page<UserJsonBulk> users = userService.friends(
                request.getUsername(),
                pageable,
                search
        );

        responseObserver.onNext(toProto(users));
        responseObserver.onCompleted();
    }

    @Override
    public void removeFriend(FriendshipRequest request, StreamObserver<Empty> responseObserver) {

        userService.removeFriend(
                request.getUsername(),
                request.getTargetUsername());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void sendInvitation(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {

        UserJson sendInvitationRequest = userService.createFriendshipRequest(
                request.getUsername(),
                request.getTargetUsername()
        );

        responseObserver.onNext(toProto(sendInvitationRequest));
        responseObserver.onCompleted();
    }

    @Override
    public void acceptInvitation(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {

        UserJson acceptInvitationRequest = userService.acceptFriendshipRequest(
                request.getUsername(),
                request.getTargetUsername()
        );

        responseObserver.onNext(toProto(acceptInvitationRequest));
        responseObserver.onCompleted();
    }

    @Override
    public void declineInvitation(FriendshipRequest request, StreamObserver<UserResponse> responseObserver) {

        UserJson declineInvitationRequest = userService.declineFriendshipRequest(
                request.getUsername(),
                request.getTargetUsername()
        );

        responseObserver.onNext(toProto(declineInvitationRequest));
        responseObserver.onCompleted();
    }

    private static UserResponse toProto(UserJson user) {
        UserResponse.Builder b = UserResponse.newBuilder();
        user.toProto(b);
        return b.build();
    }

    private static UserResponse toProto(UserJsonBulk u) {
        UserResponse.Builder b = UserResponse.newBuilder();
        u.toProto(b);
        return b.build();
    }

    private static UsersResponse toProto(List<UserJsonBulk> users) {
        UsersResponse.Builder rb = UsersResponse.newBuilder();
        for (UserJsonBulk u : users) {
            rb.addUsers(toProto(u));
        }
        return rb.build();
    }

    private static UserPageResponse toProto(Page<UserJsonBulk> page) {
        UserPageResponse.Builder b = UserPageResponse.newBuilder();

        b.setTotalElements((int) page.getTotalElements());
        b.setTotalPages(page.getTotalPages());
        b.setFirst(page.isFirst());
        b.setLast(page.isLast());
        b.setPage(page.getNumber());
        b.setSize(page.getSize());

        for (UserJsonBulk u : page.getContent()) {
            b.addContent(toProto(u));
        }
        return b.build();
    }
}
