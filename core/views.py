from rest_framework import viewsets, permissions, status
from rest_framework.decorators import action
from rest_framework.response import Response
from django.contrib.auth.models import User
from django.contrib.auth import authenticate
from .models import Profile, Skill, SwapRequest, Feedback
from .serializers import ProfileSerializer, SkillSerializer, SwapRequestSerializer, FeedbackSerializer, UserSerializer
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.views import APIView
from django.db.models import Q

class CustomAuthToken(ObtainAuthToken):
    def post(self, request, *args, **kwargs):
        response = super().post(request, *args, **kwargs)
        token = Token.objects.get(key=response.data['token'])
        return Response({'token': token.key, 'message': 'Login successful'})

class RegisterView(APIView):
    permission_classes = [permissions.AllowAny]
    
    def post(self, request):
        email = request.data.get('email')
        password = request.data.get('password')
        name = request.data.get('name', '')
        
        if not email or not password:
            return Response({'error': 'Email and password are required'}, status=status.HTTP_400_BAD_REQUEST)
        
        if User.objects.filter(email=email).exists():
            return Response({'error': 'User with this email already exists'}, status=status.HTTP_400_BAD_REQUEST)
        
        # Create user
        user = User.objects.create_user(
            username=email,
            email=email,
            password=password,
            first_name=name
        )
        
        # Create profile
        Profile.objects.create(user=user, name=name)
        
        # Create token
        token, created = Token.objects.get_or_create(user=user)
        
        return Response({
            'message': 'User registered successfully',
            'token': token.key,
            'user_id': user.id
        }, status=status.HTTP_201_CREATED)

class UserViewSet(viewsets.ReadOnlyModelViewSet):
    serializer_class = UserSerializer
    permission_classes = [permissions.AllowAny]
    
    def get_queryset(self):
        return User.objects.filter(is_active=True)
    
    def list(self, request):
        # Get public profiles only
        profiles = Profile.objects.filter(is_public=True)
        data = []
        for profile in profiles:
            data.append({
                'id': profile.user.id,
                'name': profile.name,
                'email': profile.user.email,
                'offered_skills': profile.offered_skills,
                'wanted_skills': profile.wanted_skills,
                'location': profile.location,
                'availability': profile.availability,
                'rating': profile.rating,
                'is_public': profile.is_public
            })
        return Response({'results': data})

class ProfileViewSet(viewsets.ModelViewSet):
    serializer_class = ProfileSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return Profile.objects.filter(user=self.request.user)
    
    def list(self, request):
        # Return current user's profile
        try:
            profile = Profile.objects.get(user=request.user)
            return Response({
                'id': profile.id,
                'name': profile.name,
                'email': request.user.email,
                'offered_skills': profile.offered_skills,
                'wanted_skills': profile.wanted_skills,
                'location': profile.location,
                'availability': profile.availability,
                'rating': profile.rating,
                'is_public': profile.is_public
            })
        except Profile.DoesNotExist:
            return Response({'error': 'Profile not found'}, status=status.HTTP_404_NOT_FOUND)
    
    @action(detail=False, methods=['put'])
    def update_profile(self, request):
        try:
            profile = Profile.objects.get(user=request.user)
            profile.name = request.data.get('name', profile.name)
            profile.offered_skills = request.data.get('offered_skills', profile.offered_skills)
            profile.availability = request.data.get('availability', profile.availability)
            profile.is_public = request.data.get('is_public', profile.is_public)
            profile.location = request.data.get('location', profile.location)
            profile.save()
            return Response({'message': 'Profile updated successfully'})
        except Profile.DoesNotExist:
            return Response({'error': 'Profile not found'}, status=status.HTTP_404_NOT_FOUND)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class SkillViewSet(viewsets.ModelViewSet):
    serializer_class = SkillSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return Skill.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)

class SwapRequestViewSet(viewsets.ModelViewSet):
    serializer_class = SwapRequestSerializer
    permission_classes = [permissions.IsAuthenticated]
    
    def get_queryset(self):
        # Return requests where user is either requester or target
        return SwapRequest.objects.filter(
            Q(requester=self.request.user) | Q(target_user=self.request.user)
        )
    
    def list(self, request):
        requests = self.get_queryset()
        data = []
        for req in requests:
            data.append({
                'id': req.id,
                'requester_name': req.requester.first_name or req.requester.username,
                'target_name': req.target_user.first_name or req.target_user.username,
                'message': req.message,
                'status': req.status,
                'created_at': req.created_at.strftime('%Y-%m-%d %H:%M')
            })
        return Response({'results': data})
    
    def create(self, request):
        target_user_id = request.data.get('target_user_id')
        message = request.data.get('message', 'I\'d like to swap skills with you!')
        
        try:
            target_user = User.objects.get(id=target_user_id)
            swap_request = SwapRequest.objects.create(
                requester=request.user,
                target_user=target_user,
                message=message,
                status='pending'
            )
            return Response({
                'message': 'Swap request sent successfully',
                'id': swap_request.id
            }, status=status.HTTP_201_CREATED)
        except User.DoesNotExist:
            return Response({'error': 'Target user not found'}, status=status.HTTP_404_NOT_FOUND)

class FeedbackViewSet(viewsets.ModelViewSet):
    queryset = Feedback.objects.all()
    serializer_class = FeedbackSerializer
    permission_classes = [permissions.IsAuthenticated]

class DashboardView(APIView):
    permission_classes = [permissions.IsAuthenticated]
    
    def get(self, request):
        user = request.user
        profile = Profile.objects.get(user=user)
        
        # Get statistics
        total_swaps = SwapRequest.objects.filter(
            Q(requester=user) | Q(target_user=user),
            status='completed'
        ).count()
        
        pending_requests = SwapRequest.objects.filter(
            Q(requester=user) | Q(target_user=user),
            status='pending'
        ).count()
        
        completed_swaps = SwapRequest.objects.filter(
            Q(requester=user) | Q(target_user=user),
            status='completed'
        ).count()
        
        # Get recent activity
        recent_requests = SwapRequest.objects.filter(
            Q(requester=user) | Q(target_user=user)
        ).order_by('-created_at')[:5]
        
        recent_activity = []
        for req in recent_requests:
            if req.requester == user:
                recent_activity.append(f"Swap request sent to {req.target_user.first_name or req.target_user.username}")
            else:
                recent_activity.append(f"Received swap request from {req.requester.first_name or req.requester.username}")
        
        return Response({
            'user_name': profile.name,
            'total_swaps': total_swaps,
            'pending_requests': pending_requests,
            'completed_swaps': completed_swaps,
            'recent_activity': '\n'.join(recent_activity) if recent_activity else 'No recent activity'
        })
