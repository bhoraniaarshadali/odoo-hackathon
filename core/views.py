from rest_framework import viewsets, permissions, status
from rest_framework.decorators import action
from rest_framework.response import Response
from rest_framework import generics
from rest_framework.permissions import AllowAny, IsAuthenticated
from django.contrib.auth.models import User
from django.db.models import Q
from .models import Profile, Skill, SwapRequest, Feedback
from .serializers import (
    ProfileSerializer, SkillSerializer, SwapRequestSerializer, FeedbackSerializer,
    UserRegistrationSerializer, SwapRequestCreateSerializer,
    FeedbackCreateSerializer, PublicProfileSerializer
)
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.modelPS E:\Oddo Hackthon\skillswap>    git pull origin master --rebase
error: cannot pull with rebase: You have unstaged changes.
error: Please commit or stash them.s import Token

class RegisterView(generics.CreateAPIView):
    queryset = User.objects.all()
    serializer_class = UserRegistrationSerializer
    permission_classes = [AllowAny]

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        
        # Create a profile for the new user
        Profile.objects.create(
            user=user,
            name=user.get_full_name() or user.username,
            location="",
            availability="Available"
        )
        
        # Create token for the user
        token, created = Token.objects.get_or_create(user=user)
        
        return Response({
            'user': {
                'id': user.id,
                'username': user.username,
                'email': user.email,
                'first_name': user.first_name,
                'last_name': user.last_name
            },
            'token': token.key,
            'message': 'User registered successfully'
        }, status=status.HTTP_201_CREATED)

class CustomAuthToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        response = super().post(request, *args, **kwargs)
        if response.status_code == 200:
            token = Token.objects.get(key=response.data['token'])
            user = token.user
            return Response({
                'token': token.key,
                'user': {
                    'id': user.id,
                    'username': user.username,
                    'email': user.email,
                    'first_name': user.first_name,
                    'last_name': user.last_name
                }
            })
        return response

class ProfileViewSet(viewsets.ModelViewSet):
    serializer_class = ProfileSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return Profile.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    @action(detail=False, methods=['get'])
    def public_profiles(self, request):
        """Get all public profiles with their skills"""
        profiles = Profile.objects.filter(is_public=True).prefetch_related('user__skill_set')
        serializer = PublicProfileSerializer(profiles, many=True)
        return Response(serializer.data)

    @action(detail=True, methods=['get'])
    def public_profile(self, request, pk=None):
        """Get a specific public profile"""
        try:
            profile = Profile.objects.get(id=pk, is_public=True)
            serializer = PublicProfileSerializer(profile)
            return Response(serializer.data)
        except Profile.DoesNotExist:
            return Response({'error': 'Profile not found or not public'}, status=404)

class SkillViewSet(viewsets.ModelViewSet):
    serializer_class = SkillSerializer
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return Skill.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

    @action(detail=False, methods=['get'])
    def available_skills(self, request):
        """Get all skills that are being offered by other users"""
        skills = Skill.objects.filter(is_offered=True).exclude(user=request.user)
        serializer = self.get_serializer(skills, many=True)
        return Response(serializer.data)

    @action(detail=False, methods=['get'])
    def wanted_skills(self, request):
        """Get all skills that other users want"""
        skills = Skill.objects.filter(is_offered=False).exclude(user=request.user)
        serializer = self.get_serializer(skills, many=True)
        return Response(serializer.data)

class SwapRequestViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return SwapRequest.objects.filter(
            Q(sender=self.request.user) | Q(receiver=self.request.user)
        )

    def get_serializer_class(self):
        if self.action == 'create':
            return SwapRequestCreateSerializer
        return SwapRequestSerializer

    def perform_create(self, serializer):
        serializer.save(sender=self.request.user)

    @action(detail=True, methods=['post'])
    def accept(self, request, pk=None):
        """Accept a swap request"""
        try:
            swap_request = SwapRequest.objects.get(id=pk, receiver=request.user, status='pending')
            swap_request.status = 'accepted'
            swap_request.save()
            return Response({'message': 'Swap request accepted'})
        except SwapRequest.DoesNotExist:
            return Response({'error': 'Swap request not found'}, status=404)

    @action(detail=True, methods=['post'])
    def reject(self, request, pk=None):
        """Reject a swap request"""
        try:
            swap_request = SwapRequest.objects.get(id=pk, receiver=request.user, status='pending')
            swap_request.status = 'rejected'
            swap_request.save()
            return Response({'message': 'Swap request rejected'})
        except SwapRequest.DoesNotExist:
            return Response({'error': 'Swap request not found'}, status=404)

    @action(detail=False, methods=['get'])
    def sent_requests(self, request):
        """Get all sent swap requests"""
        requests = SwapRequest.objects.filter(sender=request.user)
        serializer = SwapRequestSerializer(requests, many=True)
        return Response(serializer.data)

    @action(detail=False, methods=['get'])
    def received_requests(self, request):
        """Get all received swap requests"""
        requests = SwapRequest.objects.filter(receiver=request.user)
        serializer = SwapRequestSerializer(requests, many=True)
        return Response(serializer.data)

class FeedbackViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]

    def get_queryset(self):
        return Feedback.objects.filter(from_user=self.request.user)

    def get_serializer_class(self):
        if self.action == 'create':
            return FeedbackCreateSerializer
        return FeedbackSerializer

    def perform_create(self, serializer):
        serializer.save(from_user=self.request.user)

    @action(detail=False, methods=['get'])
    def received_feedback(self, request):
        """Get feedback received by the user"""
        # Get feedback for swaps where user was involved
        user_swaps = SwapRequest.objects.filter(
            Q(sender=request.user) | Q(receiver=request.user)
        )
        feedback = Feedback.objects.filter(swap__in=user_swaps).exclude(from_user=request.user)
        serializer = FeedbackSerializer(feedback, many=True)
        return Response(serializer.data)

class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = User.objects.all()
    serializer_class = UserRegistrationSerializer
    permission_classes = [IsAuthenticated]

    @action(detail=False, methods=['get'])
    def me(self, request):
        """Get current user information"""
        serializer = UserRegistrationSerializer(request.user)
        return Response(serializer.data)
