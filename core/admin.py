from django.contrib import admin
from .models import Profile, Skill, SwapRequest, Feedback

admin.site.register(Profile)
admin.site.register(Skill)
admin.site.register(SwapRequest)
admin.site.register(Feedback)