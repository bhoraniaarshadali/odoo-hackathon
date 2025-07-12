from django.contrib.auth.models import User
from django.db import models

class Profile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    name = models.CharField(max_length=100)
    location = models.CharField(max_length=100, blank=True, null=True)
    is_public = models.BooleanField(default=True)
    availability = models.CharField(max_length=100, blank=True, null=True)
    offered_skills = models.TextField(blank=True, null=True)
    wanted_skills = models.TextField(blank=True, null=True)
    rating = models.IntegerField(default=0)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.name

class Skill(models.Model):
    name = models.CharField(max_length=100)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    is_offered = models.BooleanField(default=True)  # True = offered, False = wanted
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.name} ({'Offer' if self.is_offered else 'Want'})"

class SwapRequest(models.Model):
    requester = models.ForeignKey(User, on_delete=models.CASCADE, related_name='sent_requests')
    target_user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='received_requests')
    message = models.TextField(default="I'd like to swap skills with you!")
    status = models.CharField(max_length=10, choices=[('pending', 'Pending'), ('accepted', 'Accepted'), ('rejected', 'Rejected'), ('completed', 'Completed')], default='pending')
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.requester.username} -> {self.target_user.username}: {self.status}"

class Feedback(models.Model):
    swap = models.ForeignKey(SwapRequest, on_delete=models.CASCADE)
    from_user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='feedbacks_from', null=True, blank=True)
    comments = models.TextField()
    rating = models.IntegerField()
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"Feedback from {self.from_user.username} on swap {self.swap.id}"